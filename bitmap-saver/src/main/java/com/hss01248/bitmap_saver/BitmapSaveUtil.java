package com.hss01248.bitmap_saver;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.permission.MyPermissions;
import com.hss01248.permission.ext.IExtPermissionCallback;
import com.hss01248.permission.ext.MyPermissionsExt;
import com.hss01248.permission.ext.permissions.StorageManagerPermissionImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Observer;

public class BitmapSaveUtil {


    public static void openConfigPage(){
        DirConfigActivity.start();
    }

    public static void setPrefix(String name){
        DirConfigInfo.setPrefix(name);
    }

    public static void saveBitmap(Bitmap bitmap) throws Exception {



        //压缩成jpg和png,然后对比大小,保留小的

        //文件名规则:
        String fileName = DirConfigInfo.filePath();
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        File dir = new File(Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"screenshot2");
        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir,fileName);

        File jpgFile = file;
        FileOutputStream fileOutputStream = new FileOutputStream(jpgFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);

        File finalFile = jpgFile;
        boolean compaired = false;
        if(compaired){
            File pngFile = new File(file.getAbsolutePath().replace(".jpg",".png"));
            FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, pngOutputStream);

            LogUtils.i("文件大小:",jpgFile.getAbsolutePath(),jpgFile.length(),pngFile.getAbsolutePath(),pngFile.length());
            if(jpgFile.length()> pngFile.length() && pngFile.length()>0){
                LogUtils.i("png文件更小,使用png格式");
                jpgFile.delete();
                finalFile = pngFile;
            }else{
                LogUtils.i("jpg文件更小,使用jpg格式");
                pngFile.delete();
            }
        }
        if(DirConfigInfo.loadConfigInfo().hiddenType ==0){
            //将文件写到mediastore
            String path = Environment.DIRECTORY_PICTURES+"/quick_screen_shot" ;
            writeToMediaStore(finalFile,path);
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+path+"/"+finalFile.getName());
            if(myFile.exists() && myFile.length() >0){
                LogUtils.i("文件成功另存到mediastore:",myFile.getAbsolutePath());
                finalFile.delete();
            }else{
                LogUtils.w("文件另存到mediastore 失败:",myFile.getAbsolutePath());
            }
        }else {
            File myFile = new File(DirConfigInfo.filePath());
            File dir2 = myFile.getParentFile();
            if(!dir2.exists()){
                dir2.mkdirs();
            }
            File file2 = new File(dir2,".nomedia");
            if(!file2.exists()){
                try {
                    file2.createNewFile();
                } catch (IOException e) {
                    LogUtils.w(e);
                }
            }
            if(DirConfigInfo.loadConfigInfo().hiddenType ==2){
                //todo 加密
                //EncryptUtils.encryptAES()
            }
            boolean copy = FileUtils.copy(finalFile, myFile);
            if(copy){
                LogUtils.i("文件另存隐藏文件夹成功:",myFile.getAbsolutePath());
                finalFile.delete();
            }else {
                LogUtils.w("文件另存隐藏文件夹失败:",myFile.getAbsolutePath());
            }
        }
    }

    private static void writeToMediaStore(File srcFile,String path) throws Exception{
// 获得ContentResolver对象
        ContentResolver resolver = Utils.getApp().getContentResolver();

        // 设置文件信息到ContentValues对象
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, srcFile.getName());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,
                srcFile.getName().endsWith(".jpg")?"image/jpeg":"image/png");
        // 根据文件类型设置
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);

        // 插入文件到系统MediaStore
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri);
                 InputStream inputStream = new FileInputStream(srcFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
            } catch (IOException e) {
                LogUtils.w(e,srcFile.getAbsolutePath());
                throw e;
            }
        } else {
            throw new IOException("Failed to create new MediaStore record: "+srcFile.getAbsolutePath());
        }
    }




    public static void askWritePermission( Observer<Boolean> callBack) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
                && Environment.isExternalStorageLegacy())){
            //android10以下
            MyPermissions.requestByMostEffort(true, false,
                    new PermissionUtils.FullCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> granted) {
                            callBack.onNext(true);
                        }

                        @Override
                        public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            callBack.onNext(false);
                        }
                    }, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            //或者Android10,兼容模式下  请求WRITE_EXTERNAL_STORAGE
         /*   PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            callBack.onNext(true);
                        }

                        @Override
                        public void onDenied() {
                            callBack.onNext(false);
                        }
                    }).request();*/

        }else {
            MyPermissionsExt.askPermission(ActivityUtils.getTopActivity(), new StorageManagerPermissionImpl(),
                    new IExtPermissionCallback() {
                        @Override
                        public void onGranted(String name) {
                            callBack.onNext(true);
                        }

                        @Override
                        public void onDenied(String name) {
                            callBack.onNext(false);
                        }
                    });
            //todo Android10,且为分区存储时(非兼容模式),权限怎么申请?
            //请求manage权限
           /* XXPermissions.with(ActivityUtils.getTopActivity())
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            callBack.onNext(true);
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            OnPermissionCallback.super.onDenied(permissions, never);
                            callBack.onNext(false);
                        }
                    });*/



            //No Activity found to handle Intent
            // Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            // intent.setPackage(AppUtils.getAppPackageName());
            // StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), intent, new ActivityResultListener() {


        }
    }
}
