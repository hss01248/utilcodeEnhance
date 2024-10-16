package com.hss01248.motion_photos_android;

import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.media.MediaStoreUtil;
import com.hss01248.motion_photos.ExifUtils;
import com.hss01248.motion_photos.MotionPhotoUtil;
import com.hss01248.toast.MyToast;

import java.io.File;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/15/24 10:48 AM
 * @Version 1.0
 */
public class AndroidMotionUtil {
    public static String removeVideo(String path) {
        if(!MotionPhotoUtil.isMotionImage(path,true)){
            return path;
        }
        String videoPath = MotionPhotoUtil.getMotionVideoPath(path);
        File file = new File(path);
        File newFile = new File(file.getParentFile(),"tmp-"+file.getName());
        if(newFile.exists()){
            newFile.delete();
        }

        try {
            newFile.createNewFile();
           // FileUtils.copy(file,newFile);
            AndroidMotionImpl.copyPartOfFile(path,newFile.getAbsolutePath(),file.length() - new File(videoPath).length());
            ExifUtils.writeXmp(newFile.getAbsolutePath(),"");

           // ExifInterface exifInterface = new ExifInterface(newFile.getAbsolutePath());
           // exifInterface.setAttribute(ExifInterface.TAG_XMP,"");
            //垃圾api,save不了
            //Android's ExifInterface, annoyingly, silently discards any data it
            // deems to be invalid. Worse yet, the documentation doesn't even mention what valid values might be.
            //exifInterface.saveAttributes();
            ExifInterface exifInterface = new ExifInterface(newFile.getAbsolutePath());
            LogUtils.d("xmp after edit:"+exifInterface.getAttribute(ExifInterface.TAG_XMP));
            //压缩图片
            return newFile.getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void forceCompressMotionPhoto(String path) {

    }

    public static void extractVideo(String path,String relativePath) {
        String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(path);
        if(motionVideoPath ==null){
            return;
        }
        //保存到mediaStore:
        File file = new File(motionVideoPath);
        if(relativePath ==null || relativePath.equals("") || relativePath.equals("null")){
            relativePath = Environment.DIRECTORY_MOVIES+"/"+file.getName();
        }else if(relativePath.startsWith(Environment.DIRECTORY_DCIM)
                || relativePath.startsWith(Environment.DIRECTORY_PICTURES)
        || relativePath.startsWith(Environment.DIRECTORY_MOVIES) ){

        }else {
            relativePath = Environment.DIRECTORY_MOVIES+"/"+file.getName();
        }

       // String relativePath = Environment.DIRECTORY_MOVIES+"/"+file.getName();

        LogUtils.d("relativePath",relativePath);

        String finalRelativePath = relativePath;
        MediaStoreUtil.writeMediaToMediaStore(file, relativePath, new MyCommonCallback3<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                MyToast.success(
                        "导出成功:"+ finalRelativePath
                );
            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {
                MyCommonCallback3.super.onError(code, msg, throwable);
                MyToast.error(msg);
            }
        });




    }

    public static void metaInfo(String initInfo) {
        //ExifUtils.readXmp(initInfo);
        try {
            ExifUtils.writeXmp(initInfo,"");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String removeVideo2(String initInfo) {
        try {
            ExifUtils.writeXmp(initInfo,"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
