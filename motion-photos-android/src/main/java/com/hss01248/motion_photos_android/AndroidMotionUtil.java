package com.hss01248.motion_photos_android;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.ContentUriUtil;
import com.hss.utils.enhance.media.MediaStoreUtil;
import com.hss01248.motion_photos.ExifUtils;
import com.hss01248.motion_photos.MotionPhotoUtil;
import com.hss01248.toast.MyToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/15/24 10:48 AM
 * @Version 1.0
 */
public class AndroidMotionUtil {
    public static String removeVideo(String originalPathOrUri) {
        if(!MotionPhotoUtil.isMotionImage(originalPathOrUri,true)){
            return originalPathOrUri;
        }

        String videoPath = MotionPhotoUtil.getMotionVideoPath(originalPathOrUri);
        String path = copyUriToCacheDir(originalPathOrUri,true);
        File file = new File(path);
        File newFile = new File(file.getParentFile(),"2tmp-"+file.getName());
        if(newFile.exists()){
            newFile.delete();
        }

        try {
            newFile.createNewFile();
           // FileUtils.copy(file,newFile);
            AndroidMotionImpl.copyPartOfFile(path,newFile.getAbsolutePath(),file.length() - new File(videoPath).length());
            new File(videoPath).delete();
            ExifUtils.writeXmp(newFile.getAbsolutePath(),"");
            ExifInterface exifInterface = new ExifInterface(newFile.getAbsolutePath());
            LogUtils.d("xmp after edit:"+exifInterface.getAttribute(ExifInterface.TAG_XMP));
            File compress = ReflectUtils.reflect("com.hss01248.img.compressor.ImageCompressor")
                    .method("compress",newFile.getAbsolutePath(), false, false).get();
            //File compress = ImageCompressor.compress(newFile.getAbsolutePath(), false, false);


            saveAsNewFileOrReplaceOriginalFile(originalPathOrUri,compress.getAbsolutePath(),
                    compress.getName().replace("2tmp-","onlyImg-"),new SaveCallback());



            //压缩图片

           // ExifInterface exifInterface = new ExifInterface(newFile.getAbsolutePath());
           // exifInterface.setAttribute(ExifInterface.TAG_XMP,"");
            //垃圾api,save不了
            //Android's ExifInterface, annoyingly, silently discards any data it
            // deems to be invalid. Worse yet, the documentation doesn't even mention what valid values might be.
            //exifInterface.saveAttributes();

            //压缩图片
            return compress.getAbsolutePath();

        } catch (Exception e) {
           MyToast.error(e.getMessage());
           return "";
        }
    }

    public static void forceCompressMotionPhoto(String path) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<File>() {
            @Override
            public File doInBackground() throws Throwable {
                String s = copyUriToCacheDir(path, true);
                boolean doNotCompressMotionPhoto = ReflectUtils.reflect("com.hss01248.img.compressor.ImageCompressor")
                        .field("doNotCompressMotionPhoto").get();
                ReflectUtils.reflect("com.hss01248.img.compressor.ImageCompressor")
                        .field("doNotCompressMotionPhoto",false).get();
                File compress = ReflectUtils.reflect("com.hss01248.img.compressor.ImageCompressor")
                        .method("compress",s, false, false).get();
                ReflectUtils.reflect("com.hss01248.img.compressor.ImageCompressor")
                        .field("doNotCompressMotionPhoto",doNotCompressMotionPhoto).get();
                return compress;
            }

            @Override
            public void onSuccess(File compress) {
                saveAsNewFileOrReplaceOriginalFile(path,compress.getAbsolutePath(),
                        compress.getName().replace("tmp-","compressed-"),new SaveCallback());
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                MyToast.error(t.getMessage());
            }
        });





    }

    public static void extractVideo(String path) {

        String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(path);
        if(motionVideoPath ==null){
            return;
        }
        doSaveAsNewFileOrReplaceOriginalFile(path,motionVideoPath,"",false,new SaveCallback());

    }

    public static void metaInfo(String initInfo) {
        //ExifUtils.readXmp(initInfo);
       // String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(initInfo);



    }

    public static   String copyUriToCacheDir(String bean,boolean alsoCopyFilePathToCache)  {
        if(bean.startsWith("content://")){

            Map<String, Object> meta =  ContentUriUtil.queryMediaStore(Uri.parse(bean));

            String name = (String) meta.get(MediaStore.MediaColumns.DISPLAY_NAME);
            if(TextUtils.isEmpty(name)){
                name = System.currentTimeMillis()+".jpg";
            }
            name = "tmp-"+name;

            File file = new File(AndroidMotionImpl.motionImageCacheDir(),name);
            InputStream stream = null;
            try {
                stream = Utils.getApp().getContentResolver().openInputStream(Uri.parse(bean));
                boolean b = FileIOUtils.writeFileFromIS(file, stream, false);
                if(b && file.exists() && file.length() >0){
                    return file.getAbsolutePath();
                }
            } catch (FileNotFoundException e) {
                LogUtils.w(e);
            }
        }else {
            if(alsoCopyFilePathToCache){
                File file = new File(bean);
                File tmp = new File(AndroidMotionImpl.motionImageCacheDir(),"tmp-"+file.getName());
                FileUtils.copy(file, tmp, new FileUtils.OnReplaceListener() {
                    @Override
                    public boolean onReplace(File srcFile, File destFile) {
                        return true;
                    }
                });
                return tmp.getAbsolutePath();
            }

        }
        return bean;
    }

    public static void saveAsNewFileOrReplaceOriginalFile(String originalPath,String srcFilePath,String newFileName,
                                                          MyCommonCallback3<String> callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUtils.getTopActivity());
        builder.setTitle("保存选项")
                .setMessage("保存为新文件还是覆盖原文件?")
                .setPositiveButton("保存为新文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doSaveAsNewFileOrReplaceOriginalFile(originalPath,srcFilePath,newFileName,false,callback);
                    }
                }).setNeutralButton("覆盖原文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doSaveAsNewFileOrReplaceOriginalFile(originalPath,srcFilePath,newFileName,true,callback);
                    }
                })
                .setNegativeButton("取消",null);
        builder.create().show();
    }

    private static void doSaveAsNewFileOrReplaceOriginalFile(String originalPath, String srcFilePath,
                                                             String newFileName, boolean overrideOriginalFile,
                                                             MyCommonCallback3<String> callback) {
        String originalFileName = "";
        String relativePath = "";
        if(originalPath.startsWith("content://")){
            Map<String, Object> meta =  ContentUriUtil.queryMediaStore(Uri.parse(originalPath));

            String name = (String) meta.get(MediaStore.MediaColumns.DISPLAY_NAME);
            if(TextUtils.isEmpty(name)){
                name = System.currentTimeMillis()+".jpg";
            }
            originalFileName = name;
            relativePath = meta.get(MediaStore.MediaColumns.RELATIVE_PATH)+"";
            if(relativePath .equals("null") || relativePath.equals("")){
                relativePath = meta.get(MediaStore.MediaColumns.DATA)+"";
                relativePath = relativePath.substring(Environment.getExternalStorageDirectory().getAbsolutePath().length()+1);
                relativePath = relativePath.substring(0,relativePath.lastIndexOf("/"));
            }
        }else {
            File file = new File(originalPath);
            originalFileName = file.getName();
            relativePath = originalPath.substring(Environment.getExternalStorageDirectory().getAbsolutePath().length()+1);
            relativePath = relativePath.substring(0,relativePath.lastIndexOf("/"));
        }

        if(overrideOriginalFile){
            //发起删除请求,先删除,再写入:
            String finalOriginalFileName = originalFileName;
            String finalRelativePath = relativePath;
            ReflectUtils.reflect("com.hss01248.fileoperation.FileDeleteUtil")
                            .method("deleteImage",originalPath,true,new Observer<Boolean>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    if(!aBoolean){
                                        callback.onError("delete file failed");
                                        return;
                                    }
                                    writeToMediaStore2(originalPath,srcFilePath, finalRelativePath,
                                            finalOriginalFileName,newFileName,overrideOriginalFile,callback);

                                }

                                @Override
                                public void onError(Throwable e) {
                                    MyToast.error(e.getMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            })
                                    .get();
        }else {
            writeToMediaStore2(originalPath,srcFilePath,relativePath, originalFileName,newFileName,overrideOriginalFile,callback);
        }
    }

    private static void writeToMediaStore2(String originalPath, String srcFilePath, String relativePath,
                                           String originalFileName, String newFileName, boolean overrideOriginalFile,
                                           MyCommonCallback3<String> callback) {
        File srcFile = new File(srcFilePath);
        String finalFileName = originalFileName;
        if(!overrideOriginalFile){
            finalFileName = newFileName;
            if(TextUtils.isEmpty(newFileName)){
                finalFileName = srcFile.getName();
            }
        }
        String finalFileName1 = finalFileName;
        MediaStoreUtil.writeMediaToMediaStore(srcFile, relativePath, finalFileName,callback);

    }


}
