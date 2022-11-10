package com.hss.utils.enhance.media;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.permission.MyPermissions;

import java.util.List;

/**
 * @author: Administrator
 * @date: 2022/11/4
 * @desc: //todo
 */
public class MediaPickUtil {

    public static void pickImage(MyCommonCallback<String> callback){
        pickOne("image/*",callback);
    }

    public static void pickVideo(MyCommonCallback<String> callback){
        pickOne("video/*",callback);
    }

    public static void pickAudio(MyCommonCallback<String> callback){
        pickOne("audio/*",callback);
    }

    public static void pickOne(String mimeType,MyCommonCallback<String> callback){
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        startIntent(mimeType, callback);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        callback.onError("permission","[read external storage] permission denied",null);
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private static void startIntent(String mimeType, MyCommonCallback<String> callback) {
        //https://www.cnblogs.com/widgetbox/p/7503894.html
        Intent intent =  new Intent();
        //intent.setType("video/*;image/*");//同时选择视频和图片
        intent.setType(mimeType);//
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //打开方式有两种action，1.ACTION_PICK；2.ACTION_GET_CONTENT 区分大意为：
        // ACTION_PICK 为打开特定数据一个列表来供用户挑选，其中数据为现有的数据。而 ACTION_GET_CONTENT 区别在于它允许用户创建一个之前并不存在的数据。
        intent.setAction(Intent.ACTION_PICK);
        //startActivityForResult(Intent.createChooser(intent,"选择图像..."), PICK_IMAGE_REQUEST);
        //FragmentManager: Activity result delivered for unknown Fragment
        StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), intent, new ActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

                if(resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    //content://com.android.providers.media.documents/document/video%3A114026
                    LogUtils.i(uri);
                    //content://media/external/video/media/103988
                    // content://com.android.fileexplorer.myprovider/external_files/qqmusic/song/萧敬腾&张杰&袁娅维
                    ContentResolver cr = Utils.getApp().getContentResolver();
                    /** 数据库查询操作。
                     * 第一个参数 uri：为要查询的数据库+表的名称。
                     * 第二个参数 projection ： 要查询的列。
                     * 第三个参数 selection ： 查询的条件，相当于SQL where。
                     * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
                     * 第四个参数 sortOrder ： 结果排序。
                     */
                    Cursor cursor = cr.query(uri, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String path = "";
                            if(mimeType.startsWith("video/")){
                                // 视频ID:MediaStore.Audio.Media._ID
                                int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                // 视频名称：MediaStore.Audio.Media.TITLE
                                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                                // 视频路径：MediaStore.Audio.Media.DATA
                                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                                // 视频时长：MediaStore.Audio.Media.DURATION
                                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                                // 视频大小：MediaStore.Audio.Media.SIZE
                                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                                // 视频缩略图路径：MediaStore.Images.Media.DATA
                                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                                // 缩略图ID:MediaStore.Audio.Media._ID
                                int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                // 方法一 Thumbnails 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                                // 第一个参数为 ContentResolver，第二个参数为视频缩略图ID， 第三个参数kind有两种为：MICRO_KIND和MINI_KIND 字面意思理解为微型和迷你两种缩略模式，前者分辨率更低一些。
                                //Bitmap bitmap1 = MediaStore.Video.Thumbnails.getThumbnail(cr, imageId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

                                // 方法二 ThumbnailUtils 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                                // 第一个参数为 视频/缩略图的位置，第二个依旧是分辨率相关的kind
                                //Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                                // 如果追求更好的话可以利用 ThumbnailUtils.extractThumbnail 把缩略图转化为的制定大小
//                        ThumbnailUtils.extractThumbnail(bitmap, width,height ,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                                path = videoPath;
                            }else if(mimeType.startsWith("image/")){
                                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                            }else if(mimeType.startsWith("audio/")){
                                //content://com.android.fileexplorer.myprovider/external_files/qqmusic/song/%E8%90%A7%E6%95%AC%E8%85%BE%
                                //.IllegalArgumentException: column '_data' does not exist. Available columns: [_display_name, _size]
                                //path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                                String url = uri.toString();
                                if(!TextUtils.isEmpty(url) && url.contains("/external_files")){
                                    path = url.substring(url.indexOf("/external_files")+"/external_files".length());
                                }

                            }
                            if(!TextUtils.isEmpty(path)){
                                LogUtils.w(path);
                                callback.onSuccess(path);
                                return;
                            }


                        }
                        cursor.close();
                    }
                    callback.onError("","not found",new Throwable("file not found"));
                    //todo 用另一个action
                }else {
                    callback.onError("","cancel",null);
                }
            }

            @Override
            public void onActivityNotFound(Throwable e) {
                callback.onError("","ActivityNotFound",e);
                //todo 用另一个action
            }
        });
    }
}
