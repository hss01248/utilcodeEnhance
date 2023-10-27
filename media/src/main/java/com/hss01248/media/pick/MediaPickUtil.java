package com.hss01248.media.pick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentResolverCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.activityresult.TheActivityListener;
import com.hss01248.media.uri.ContentUriUtil;
import com.hss01248.permission.MyPermissions;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: Administrator
 * @date: 2022/11/4
 * @Despciption 通过intent 筛选多种文件: 参考: flutter filePicker:
 * https://github.com/miguelpruivo/flutter_file_picker/blob/master/android/src/main/java/com/mr/flutter/plugin/filepicker/FilePickerDelegate.java
 *  Android 13 选媒体文件需要细化权限: https://developer.android.com/about/versions/13/behavior-changes-13
 *  系统有单独的选择器: https://developer.android.com/about/versions/13/behavior-changes-13
 *  Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
 *
 *  其他一些自定义ui的文件选择器:
 *  https://github.com/rosuH/AndroidFilePicker
 *  https://github.com/DyncKathline/FilePicker
 *  以及文件操作大综合:
 *  https://github.com/javakam/FileOperator
 *
 *  Document files (word, pdf, excel,...) are no longer returned from MediaStore.Files.getContentUri("external") since android 11
 *
 *
 * https://www.jianshu.com/p/c5f207f8cce6  uri action适配,这篇不错
 *
 *  todo https://www.jianshu.com/p/a8e8a57f41f1
 */
public class MediaPickUtil {

    public static void pickImage(MyCommonCallback<Uri> callback) {
        pickOne(callback,"image/*");
    }

    public static void pickVideo(MyCommonCallback<Uri> callback) {
        pickOne( callback,"video/*");
    }

    public static void pickAudio(MyCommonCallback<Uri> callback) {
        pickOne( callback,"audio/*");
    }


    @Deprecated
    public static void pickPdf(MyCommonCallback<Uri> callback) {
        pickOne( callback,"application/pdf");
    }
    public static void pickOneFile(MyCommonCallback<Uri> callback) {
        pickOne( callback,"*/*");
    }
    /**
     * 多文件选择特点: 大多数响应的intent只返回单个文件,不支持多文件选择
     * 一些系统应用比如相册会支持多文件选择
     * 文件类型不便限定,需要自行再callback里处理
     * @param callback
     */
    public static void pickMultiFiles(MyCommonCallback<List<Uri>> callback) {
        pickMulti( callback,"*/*");
    }


    public static void pickMulti( MyCommonCallback<List<Uri>> callback,String... mimeTypes) {
        String mimeType = MimeTypeUtil.buildMimeTypeWithDot(mimeTypes);
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        startIntent2(mimeType, callback);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        callback.onError("permission", "[read external storage] permission denied", null);
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * ACTION_PICK is not documented to support EXTRA_ALLOW_MULTIPLE. Specifically:
     *
     * The documentation for ACTION_PICK does not mention any supported extras
     *
     * The documentation for EXTRA_ALLOW_MULTIPLE only mentions ACTION_GET_CONTENT and ACTION_OPEN_DOCUMENT
     * @param mimeType
     * @param callback
     */
    private static void startIntent2(String mimeType, MyCommonCallback<List<Uri>> callback) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//打开多个文件
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        StartActivityUtil.startActivity(ActivityUtils.getTopActivity(),null, intent,true, new TheActivityListener<Activity>(){
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if (resultCode == Activity.RESULT_OK) {
                    //Get the Uri of the selected file

                    List<Uri> uris = new ArrayList<>();
                    if(data.getClipData() != null) {//有选择多个文件
                        int count = data.getClipData().getItemCount();
                        LogUtils.i("url count ：  "+ count);
                        int currentItem = 0;
                        while(currentItem < count) {
                            Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                            //String imgpath = ContentUriUtil.getPath(this,imageUri);
                            uris.add(imageUri);
                            //LogUtils.i("url "+ imageUri);

                            //do something with the image (save it to some directory or whatever you need to do with it here)
                            currentItem = currentItem + 1;
                        }
                        callback.onSuccess(uris);

                    } else if(data.getData() != null) {//只有一个文件咯
                        uris.add(data.getData());
                        LogUtils.i("Single image path ---- "+ data.getData());
                        callback.onSuccess(uris);
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }else {
                        callback.onError("result ok, but no data");
                    }

                }else {
                    callback.onError("canceled");
                }
            }

            @Override
            public void onActivityNotFound(Throwable e) {
                callback.onError(e.getClass().getSimpleName(),e.getMessage(),e);
            }
        });

    }

    /**
     * 虽然高版本Android不再需要READ_EXTERNAL_STORAGE就可以查看选择的图片,但用户不知道啊,能多拿权限就多拿
     *  Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? Manifest.permission.INTERNET : Manifest.permission.READ_EXTERNAL_STORAGE
     * @param callback
     * @param mimeTypes
     */
    public static void pickOne( MyCommonCallback<Uri> callback,String... mimeTypes) {
        String mimeType = MimeTypeUtil.buildMimeTypeWithDot(mimeTypes);
        LogUtils.i("request mimetype: "+mimeType);
        String mime = mimeTypes[0];
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            if(TextUtils.isEmpty(mimeType)){

            }else if(mime.startsWith("image")){
                permission = Manifest.permission.READ_MEDIA_IMAGES;
            }else if(mime.startsWith("video")){
                permission = Manifest.permission.READ_MEDIA_VIDEO;
            }else if(mime.startsWith("audio")){
                //permission = Manifest.permission.READ_MEDIA_AUDIO;
                //TMD 设置页面根本没有对应的权限
            }
        }
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        startIntent(mimeType, callback);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        callback.onError("permission", "[read external storage] permission denied", null);
                    }
                }, permission,Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * @param mimeTypes
     * @param callback string可能为文件路径,可能为fileprovider形式的uri,
     *                 如果是content://协议的uri,那么会去查询真正的path
     */
    private static void startIntent(String mimeTypes, MyCommonCallback<Uri> callback) {
        //https://www.cnblogs.com/widgetbox/p/7503894.html
        Intent intent = new Intent();
        //intent.setType("video/*;image/*");//同时选择视频和图片
        intent.setType(mimeTypes);//
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //打开方式有两种action，1.ACTION_PICK；2.ACTION_GET_CONTENT 区分大意为：
        // ACTION_PICK 为打开特定数据一个列表来供用户挑选，其中数据为现有的数据。而 ACTION_GET_CONTENT 区别在于它允许用户创建一个之前并不存在的数据。
        intent.setAction(Intent.ACTION_PICK);
        //startActivityForResult(Intent.createChooser(intent,"选择图像..."), PICK_IMAGE_REQUEST);
        //FragmentManager: Activity result delivered for unknown Fragment
        PackageManager manager = Utils.getApp().getPackageManager();
        if (manager.queryIntentActivities(intent, 0).size() <= 0) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //intent.setType(mimeTypes);
            String type = "*/*";
            final Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator);
            //Log.d(TAG, "Selected type " + type);
            intent.setDataAndType(uri, "*/*");
            //这里的type为application/pdf时,无法显示选项,必须为*/*
            //也不能加上putExtra(Intent.EXTRA_MIME_TYPES的限定,否则也没有选项
            //String[] mimeTypes2 = {"application/pdf"};
            //intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes2);

            //Intent { act=android.intent.action.GET_CONTENT cat=[android.intent.category.OPENABLE] typ=image/png }
        }
        if (manager.queryIntentActivities(intent, 0).size() <= 0) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeTypes);
            //    String[] mimeTypes = {doc, docx, pdf, image1,image2};
            //            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            //            intent.setType("*/*");
        }
        LogUtils.d(intent);


        StartActivityUtil.startActivity(ActivityUtils.getTopActivity(),null, intent,true, new TheActivityListener<Activity>() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                LogUtils.i(data);
                if (resultCode == RESULT_CANCELED) {
                    callback.onError("-1", "cancel", null);
                    return;
                }
                if (data == null || data.getData() == null) {
                    callback.onError("-2", "data is null", null);
                    return;
                }

                Uri uri = data.getData();
                callback.onSuccess(uri);
                //后续操作:
                //ContentUriUtil.getRealPath(uri);
                ContentUriUtil.getInfos(uri);
                //ContentUriUtil.queryMediaStore(uri);
                //content://com.android.providers.media.documents/document/video%3A114026
            }

            @Override
            public void onActivityNotFound(Throwable e) {
                callback.onError("", "ActivityNotFound", e);
                //todo 用另一个action
            }
        });
    }


}
