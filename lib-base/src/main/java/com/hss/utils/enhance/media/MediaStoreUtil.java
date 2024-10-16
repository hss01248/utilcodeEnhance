package com.hss.utils.enhance.media;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss01248.permission.MyPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaStoreUtil {


    public static void writeMediaToMediaStore(File finalFile,
                                              String albumRelativePath,
                                              MyCommonCallback3<String> callback){
        writeMediaToMediaStore(finalFile,albumRelativePath,"",callback);

    }

    /**
     *
     * @param finalFile
     * @param albumRelativePath  allowed directories are [DCIM, Movies, Pictures]
     * @param callback
     */
    public static void writeMediaToMediaStore(File finalFile,
                                              String albumRelativePath,
                                              String newFileName,
                                              MyCommonCallback3<String> callback) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
                && !Environment.isExternalStorageLegacy())){
            writeToMediaStore( finalFile, albumRelativePath,newFileName,callback);
        }else {
            File finalFile1 = finalFile;
            MyPermissions.requestByMostEffort(false, true,
                    new PermissionUtils.FullCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> granted) {
                            try {
                                writeToMediaStore( finalFile1, albumRelativePath,newFileName,callback);
                            } catch (Exception e) {
                                LogUtils.w(e);
                                callback.onError(e);
                                //MyToast.error(e.getMessage());
                            }
                        }

                        @Override
                        public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            callback.onError("permission denied");

                        }
                    }, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
            + File.separator + AppUtils.getAppName()+ File.separator+srcFile.getName());
} else {
    contentValues.put(
            MediaStore.MediaColumns.DATA,
            Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Environment.DIRECTORY_PICTURES
                    + File.separator + AppUtils.getAppName()+ File.separator+srcFile.getName()
    );
}*/

    private static void writeToMediaStore(File srcFile,String albumRelativePath, String newFileName,MyCommonCallback3<String> callback) {

        if(TextUtils.isEmpty(newFileName)){
            newFileName = srcFile.getName();
        }
        LogUtils.d("file: "+srcFile.getAbsolutePath(),albumRelativePath,newFileName);
        // 根据文件类型设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 获得ContentResolver对象
            ContentResolver resolver = Utils.getApp().getContentResolver();

            // 设置文件信息到ContentValues对象
            String name = srcFile.getName();
            name = name.substring(name.lastIndexOf(".")+1).toLowerCase();
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(name);
            if(mimeType ==null){
                mimeType = "image/jpeg";
            }
            ContentValues contentValues = new ContentValues();

            //判断是否已经存在,如果存在?


            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);


            //权限: 没有任何权限时,只有dcim,picture,movies这三个可写
            if(albumRelativePath.startsWith(Environment.DIRECTORY_MOVIES)
            || albumRelativePath.startsWith(Environment.DIRECTORY_DCIM)
             || albumRelativePath.startsWith(Environment.DIRECTORY_DOWNLOADS)
            ||albumRelativePath.startsWith(Environment.DIRECTORY_PICTURES)){

            }else {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+albumRelativePath,newFileName);
                boolean canWrite = false;
                if(file.exists()){
                    canWrite = file.canWrite();
                }else {
                    try {
                        file.createNewFile();
                        canWrite = true;
                    } catch (IOException e) {
                       canWrite = false;
                    }
                }
                if(!canWrite){
                    if(mimeType.startsWith("image")){
                        albumRelativePath = Environment.DIRECTORY_DCIM+"/"+albumRelativePath;
                    }else if(mimeType.startsWith("video")){
                        albumRelativePath = Environment.DIRECTORY_MOVIES+"/"+albumRelativePath;
                    }else {
                        albumRelativePath = Environment.DIRECTORY_DOWNLOADS+"/"+albumRelativePath;
                    }
                }
            }
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, albumRelativePath);
            //contentValues.put(MediaStore.MediaColumns.DATA, albumRelativePath);



            Uri root = null;
            if(mimeType.startsWith("image")){
                root = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }else  if(mimeType.startsWith("video")){
                root = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }else  if(mimeType.startsWith("audio")){
                root = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }else {
                LogUtils.w("mimetype is not media: ",mimeType,srcFile.getAbsolutePath());
                root = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }

            // values.put(MediaStore.MediaColumns.IS_PENDING, 0);

            //Primary directory (invalid) not allowed for content://media/external/video/media;
            // allowed directories are [DCIM, Movies, Pictures]
            Uri uri = resolver.insert(root, contentValues);
            LogUtils.i("uri :",uri);
            if (uri != null) {
                ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
                    @Override
                    public String doInBackground() throws Throwable {
                        try (OutputStream outputStream = resolver.openOutputStream(uri);
                             InputStream inputStream = new FileInputStream(srcFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            outputStream.flush();
                            inputStream.close();
                            outputStream.close();
                            Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            callback.onSuccess(uri.toString());

                        } catch (IOException e) {
                            LogUtils.w(e,srcFile.getAbsolutePath());
                            callback.onError(e);
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onFail(Throwable t) {
                        super.onFail(t);
                        callback.onError(t);
                    }
                });

            } else {
                callback.onError("Failed to create new MediaStore record: "+srcFile.getAbsolutePath());
               // throw new IOException("Failed to create new MediaStore record: "+srcFile.getAbsolutePath());
            }
        } else {
            //Failed to create new MediaStore record: /storage/emulated/0/Android/data/com.hss.utilsenhance/files/Pictures/screenshot2/2024-07-09_14-50-59.jpg

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/"+albumRelativePath+"/"+newFileName);
            LogUtils.i("file path: "+file.getAbsolutePath());
            File parentFile = file.getParentFile();
            if(parentFile.exists()){
                if(parentFile.isFile()){
                    parentFile.delete();
                }
            }
            parentFile.mkdirs();
            //直接用file api写文件. uri在老版本一堆问题.
            //垃圾代码,有bug
            boolean copy = FileUtils.copy(srcFile, file);

            //boolean copy = writeFileFromIS(file, new FileInputStream(srcFile),false,null);
            LogUtils.i("file copy success: "+copy);
            if(copy && file.exists() && file.length() > 0){
                //然后通知mediastore扫描.
                callback.onSuccess(file.getAbsolutePath());
                Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }else {
                callback.onError("copy file failed");
                Utils.getApp().sendBroadcast(new  Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(srcFile)));

            }


        }

        //java.lang.SecurityException: Permission Denial: writing com.android.providers.media.MediaProvider
        // uri content://media/external/images/media from pid=5007, uid=10083
        // requires android.permission.WRITE_EXTERNAL_STORAGE, or grantUriPermission()
        // 插入文件到系统MediaStore

    }
}

