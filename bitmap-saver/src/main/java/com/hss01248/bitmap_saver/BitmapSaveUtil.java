package com.hss01248.bitmap_saver;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.media.MediaStoreUtil;
import com.hss01248.glide.aop.file.AddByteUtil;
import com.hss01248.permission.MyPermissions;
import com.hss01248.permission.ext.IExtPermissionCallback;
import com.hss01248.permission.ext.MyPermissionsExt;
import com.hss01248.permission.ext.permissions.StorageManagerPermissionImpl;
import com.hss01248.viewholder_media.FileTreeViewHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;

public class BitmapSaveUtil {


    public static void openConfigPage(){
        DirConfigActivity.start();
    }

    public static void viewImages(boolean hidden){
        File dir = DirConfigInfo.parentDir(hidden ? 1: 0);
        FileTreeViewHolder.viewDirInActivity(dir.getAbsolutePath());
    }

    public static void setPrefix(String name){
        DirConfigInfo.setPrefix(name);
    }


    static  List<BitmapSaveListener> listeners =  new ArrayList<>();

    public static  void addBitmapSaveListener(BitmapSaveListener listener){
        listeners.add(listener);
    }

    public static  void remove(BitmapSaveListener listener){
        listeners.remove(listener);
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
        //写exif
        writeDateTimeToFile(finalFile);

        if(DirConfigInfo.loadConfigInfo().hiddenType ==0){
            //将文件写到mediastore
            String albumRelativePath = Environment.DIRECTORY_PICTURES+"/quick_screen_shot" ;
            //未开启分区存储前,需要写存储的权限:
            writeImageToMediaStore(bitmap, finalFile, albumRelativePath);

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
                AddByteUtil.addByte(finalFile.getAbsolutePath());
            }
            boolean copy = FileUtils.copy(finalFile, myFile);
            if(copy){

                notifyListeners(myFile,bitmap.getWidth(),bitmap.getHeight());
                LogUtils.i("文件另存隐藏文件夹成功:",myFile.getAbsolutePath());
                finalFile.delete();
            }else {
                LogUtils.w("文件另存隐藏文件夹失败:",myFile.getAbsolutePath());
            }
        }
    }

    public static void writeImageToMediaStore(@Nullable Bitmap bitmap, File finalFile, String albumRelativePath) throws Exception {

        MediaStoreUtil.writeMediaToMediaStore(finalFile, albumRelativePath, new MyCommonCallback3<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ albumRelativePath +"/"+ finalFile.getName());
                if(myFile.exists() && myFile.length() >0){
                    LogUtils.i("文件成功另存到mediastore:",myFile.getAbsolutePath());
                    if(bitmap !=null){
                        notifyListeners(myFile, bitmap.getWidth(), bitmap.getHeight());
                    }
                    finalFile.delete();
                }else{
                    LogUtils.w("文件另存到mediastore 失败:",myFile.getAbsolutePath());
                }
            }
        });

    }



    private static void notifyListeners(File myFile, int width, int height) {
        for (BitmapSaveListener listener : listeners) {
            try{
                listener.onSaved(myFile,width,height);
            }catch (Throwable throwable){
                LogUtils.w(throwable);
            }

        }
    }

    private static void writeDateTimeToFile(File file) {
        try {
            ExifInterface exifInterface = new ExifInterface(file);
            //exifInterface.setDateTime(System.currentTimeMillis());
            long timeStamp = System.currentTimeMillis();
            SimpleDateFormat sFormatterPrimary = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US);
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, sFormatterPrimary.format(new Date(timeStamp)));
            exifInterface.saveAttributes();
        } catch (Exception e) {
           LogUtils.w(e);
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
