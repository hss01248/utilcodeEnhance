package com.hss.utils.enhance.media;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.permission.MyPermissions;
import com.hss01248.toast.MyToast;

import java.io.File;
import java.util.List;

/**
 * @Despciption 通过intent 筛选多种文件: 参考: flutter filePicker:
 * https://github.com/miguelpruivo/flutter_file_picker/blob/master/android/src/main/java/com/mr/flutter/plugin/filepicker/FilePickerDelegate.java
 *  Android 13 选媒体文件需要细化权限: https://developer.android.com/about/versions/13/behavior-changes-13
 *  系统有单独的选择器: https://developer.android.com/about/versions/13/behavior-changes-13
 *  Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
 *
 *  其他一些自定义ui的文件选择器:
 *  https://github.com/rosuH/AndroidFilePicker
 *  https://github.com/DyncKathline/FilePicker
 * @Author hss
 * @Date 11/11/2022 09:52
 * @Version 1.0
 */
public class FilePickUtil {

    public static void pickPdf(Activity activity){
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {

                        /*if (type.equals("dir")) {
                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        }*/
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        //intent.setType("application/pdf");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);


                        String type = "*/*";
                        boolean isMultipleSelection = false;
                        String[] extentions = {"application/pdf"};

                        final Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator);
                        //Log.d(TAG, "Selected type " + type);
                        intent.setDataAndType(uri, type);
                        intent.setType(type);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultipleSelection);
                        intent.putExtra("multi-pick", isMultipleSelection);

                         //intent.putExtra(Intent.EXTRA_MIME_TYPES, extentions);

                         /*if (intent.resolveActivity(this.activity.getPackageManager()) != null) {
                            this.activity.startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            Log.e(TAG, "Can't find a valid activity to handle the request. Make sure you've a file explorer installed.");
                            finishWithError("invalid_format_type", "Can't handle the provided file type.");
        }*/

                        StartActivityUtil.goOutAppForResult(activity, intent, new ActivityResultListener() {
                            @Override
                            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                                LogUtils.d(data);
                            }

                            @Override
                            public void onActivityNotFound(Throwable e) {

                            }
                        });
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        MyToast.error("permission denied");
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * 调用系统选择文件，支持pdf doc docx image
     *
     * @param activity
     */
    public static void systemFile(Activity activity) {
        try {

            /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
     intent.setType("application/pdf");
     intent.addCategory(Intent.CATEGORY_OPENABLE);*/

            //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            String pdf = "application/pdf";
            String doc = "application/msword";
            String docx = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            String image1 = "image/png";
            String image2 = "image/jpeg";


            String[] mimeTypes = {doc, docx, pdf, image1,image2};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);


            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            StartActivityUtil.goOutAppForResult(activity, intent, new ActivityResultListener() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                    LogUtils.d(data);
                }

                @Override
                public void onActivityNotFound(Throwable e) {

                }
            });
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static class MimeType {
        public static final String DOC = "application/msword";
        public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String XLS = "application/vnd.ms-excel application/x-excel";
        public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String PPT = "application/vnd.ms-powerpoint";
        public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        public static final String PDF = "application/pdf";
    }

    private static String resolveType(final String type) {

        switch (type) {
            case "audio":
                return "audio/*";
            case "image":
                return "image/*";
            case "video":
                return "video/*";
            case "media":
                return "image/*,video/*";
            case "any":
            case "custom":
                return "*/*";
            case "dir":
                return "dir";
            default:
                return null;
        }
    }

}
