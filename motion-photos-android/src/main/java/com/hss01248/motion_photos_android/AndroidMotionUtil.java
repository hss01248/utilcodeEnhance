package com.hss01248.motion_photos_android;

import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.motion_photos.ExifUtils;
import com.hss01248.motion_photos.MotionPhotoUtil;

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

    public static void extractVideo(String path) {
        String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(path);
        //保存到mediaStore:



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
