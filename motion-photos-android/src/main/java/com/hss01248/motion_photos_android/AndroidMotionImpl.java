package com.hss01248.motion_photos_android;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.motion_photos.IMotion;
import com.hss01248.motion_photos.MotionPhotoUtil;
import com.hss01248.videocompress.CompressType;
import com.hss01248.videocompress.VideoCompressUtil;
import com.hss01248.videocompress.listener.ICompressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Despciption todo
 * @Author hss
 * @Date 9/18/24 11:41 AM
 * @Version 1.0
 */
public class AndroidMotionImpl implements IMotion {
    @Override
    public long length(String fileOrUriPath) throws Throwable {
        InputStream stream = null;
        try {
            stream =   steam(fileOrUriPath);
            if(stream ==null){
                return 0;
            }
            return stream.available();
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return 0;
        }finally {
            if(stream !=null){
                stream.close();
            }
        }
    }

    @Override
    public String readXmp(String fileOrUriPath) throws Throwable{
        InputStream stream = null;
        try {
            stream =   steam(fileOrUriPath);
            if(stream ==null){
                return null;
            }
            ExifInterface exifInterface = new ExifInterface(stream);
            return exifInterface.getAttribute(ExifInterface.TAG_XMP);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return null;
        }finally {
            if(stream !=null){
                stream.close();
            }
        }
    }

    InputStream steam(String fileOrUriPath) throws Throwable{
        if(fileOrUriPath.startsWith("content://")){
            Uri uri = Uri.parse(fileOrUriPath);
            return Utils.getApp().getContentResolver().openInputStream(uri);
        }else if(fileOrUriPath.startsWith("file://")){
            Uri uri = Uri.parse(fileOrUriPath);
            File file = new File(uri.getPath());
            if(file.exists() && file.length() >0){
                return new FileInputStream(file);
            }
        }else if(fileOrUriPath.startsWith("http://") || fileOrUriPath.startsWith("https://")){
            //下载到本地....

        }else {
            File file = new File(fileOrUriPath);
            if(file.exists() && file.length() >0){
                return new FileInputStream(file);
            }
        }
        return null;
    }

    @Override
    public String mp4CacheFile(String path) {
       File dir = new File(Utils.getApp().getExternalCacheDir(),"motion-videos") ;
       if(!dir.exists()){
           dir.mkdirs();
       }
       File file = new File(path);
       File file2 = new File(dir,file.getName()+".mp4");
        return file2.getAbsolutePath();
    }

    @Override
    public Map<String, Object> metaOfImage(String fileOrUriPath) {
        InputStream stream = null;
        try {
            stream =   steam(fileOrUriPath);
            if(stream ==null){
                return null;
            }
            Map<String, Object> map = new TreeMap<>();
            ExifInterface exifInterface = new ExifInterface(stream);
            Field[] fields = ExifInterface.class.getDeclaredFields();
            for (Field field : fields) {
                if(field.getName().startsWith("TAG_")){
                    field.setAccessible(true);
                    String  str = field.get(ExifInterface.class)+"";
                    map.put(str,exifInterface.getAttribute(str));

                }
            }
            return map;
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return null;
        }finally {
            if(stream !=null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Map<String, Object> metaOfVideo(String fileOrUriPath) {

        try {
            Map<String, Object> map = new TreeMap<>();
            MediaMetadataRetriever exifInterface = new MediaMetadataRetriever();
            exifInterface.setDataSource(fileOrUriPath);
            Field[] fields = MediaMetadataRetriever.class.getDeclaredFields();
            for (Field field : fields) {
                if(field.getName().startsWith("METADATA_KEY_")){
                    field.setAccessible(true);
                    int   str = (int) field.get(ExifInterface.class);
                    map.put(field.getName().substring("METADATA_KEY_".length()).toLowerCase(),exifInterface.extractMetadata(str));
                }
            }
            return map;
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return null;
        }
    }

    public static File compressMp4File(String fileOrUriPath){

        File dir = new File(Utils.getApp().getExternalCacheDir(),"motion-videos") ;
        File file = new File(fileOrUriPath);
        final File[] file2 = {new File(dir, "out-" + file.getName())};

        CountDownLatch latch = new CountDownLatch(1);
        VideoCompressUtil.doCompress(false,fileOrUriPath, dir.getAbsolutePath(), CompressType.TYPE_UPLOAD_1080P,
                new ICompressListener() {
                    @Override
                    public void onFinish(String outputFilePath) {
                        LogUtils.d("compress finished: ",outputFilePath, file2[0].getAbsolutePath());
                        file2[0] = new File(outputFilePath);
                        if(file.length() <= file2[0].length()){
                            LogUtils.w("压缩后文件变大",fileOrUriPath,outputFilePath);
                            file2[0] = file;
                        }

                        latch.countDown();
                    }

                    @Override
                    public void onError(String message) {
                        LogUtils.d("compress failed: ",message);
                        latch.countDown();
                    }
                });
        try {
            boolean await = latch.await(6, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LogUtils.w(e,fileOrUriPath);
        }
        LogUtils.d("compress return: ", file2[0].getAbsolutePath());
        return file2[0];

    }

    public static File replaceVideoFile(String imagePath,String newVideoPath) throws Exception{
        File imageFile = new File(imagePath);
        //复制文件到私有目录,便于操作:
        File dir = new File(Utils.getApp().getExternalCacheDir(),"motion-videos") ;
        if(!dir.exists()){
            dir.mkdirs();
        }
        File imageFileTmp = new File(dir,imageFile.getName());
        boolean copy = FileUtils.copy(imageFile, imageFileTmp, new FileUtils.OnReplaceListener() {
            @Override
            public boolean onReplace(File srcFile, File destFile) {
                return true;
            }
        });
        LogUtils.i("文件拷贝成功:"+copy);
        imageFile = imageFileTmp;


        String mp4 = MotionPhotoUtil.getMotionVideoPath(imagePath);
        File originalMp4File = new File(mp4);

        File mp4Compressed = new File(newVideoPath);

        if(mp4Compressed !=null && mp4Compressed.exists() && mp4Compressed.length() >0){
            long length = mp4Compressed.length();
            //保存为谷歌格式/小米格式/原格式
            if(length != originalMp4File.length()){
                //更改exif
                ExifInterface exifInterface1 = new ExifInterface(imageFileTmp.getAbsolutePath());
                String xmp = exifInterface1.getAttribute(ExifInterface.TAG_XMP);
                LogUtils.i("xmp before",xmp);
                xmp = xmp.replace(originalMp4File.length()+"",length+"");
                LogUtils.i("xmp after",xmp);
                exifInterface1.setAttribute(ExifInterface.TAG_XMP,xmp);
                exifInterface1.setAttribute(MotionPhotoUtil.customExifKey,length+"");
                //写exif, 需要写文件权限,一般没有
                //如果文件被其他应用或进程锁定，ExifInterface 可能无法写入文件。确保文件没有被其他进程使用
                exifInterface1.saveAttributes();

            }

            long lengthOriginal = imageFileTmp.length();
            long lengthToCopy = lengthOriginal - originalMp4File.length();
            File tmp = new File(imageFileTmp.getParentFile(),"tmp-"+imageFileTmp.getName());
            if(tmp.exists()){
                tmp.delete();
            }
            tmp.createNewFile();
            //拷贝图片部分
            copyFirstNBytes(imageFileTmp.getAbsolutePath(),tmp.getAbsolutePath(), (int) lengthToCopy);


            //追加视频部分:
            FileIOUtils.writeFileFromIS(tmp,new FileInputStream(mp4Compressed),true);
            //appendToFile(mp4Compressed.getAbsolutePath(),tmp.getAbsolutePath());

            mp4Compressed.delete();
            originalMp4File.delete();
            imageFileTmp.delete();
            return tmp;
        }
        return imageFile;
    }


    /**
     * 将一个文件的内容追加到另一个文件的尾部。
     *
     * @param sourceFilePath 源文件路径
     * @param destFilePath   目标文件路径
     * @throws IOException 如果发生I/O错误
     */
    public static void appendToFile(String sourceFilePath, String destFilePath) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFilePath);
            fos = new FileOutputStream(destFilePath, true); // 第二个参数为true表示追加模式

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }





        /**
         * Copies the first n bytes of a source file to a destination file.
         *
         * @param sourceFilePath the path of the source file
         * @param destFilePath   the path of the destination file
         * @param n              the number of bytes to copy
         * @throws IOException if an I/O error occurs
         */
        public static void copyFirstNBytes(String sourceFilePath, String destFilePath, int n) throws IOException {
            File sourceFile = new File(sourceFilePath);

            // Pre-checks
            if (!sourceFile.exists()) {
                throw new IllegalArgumentException("Source file does not exist.");
            }

            if (!sourceFile.isFile()) {
                throw new IllegalArgumentException("Source path does not point to a file.");
            }

            if (n < 0) {
                throw new IllegalArgumentException("Number of bytes to copy cannot be negative.");
            }

            FileInputStream fis = null;
            FileOutputStream fos = null;

            try {
                fis = new FileInputStream(sourceFile);
                fos = new FileOutputStream(destFilePath);

                byte[] buffer = new byte[n];
                int bytesRead = fis.read(buffer, 0, n);

                if (bytesRead > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
            } finally {
                // Ensure streams are closed in the finally block
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        System.err.println("Failed to close FileInputStream: " + e.getMessage());
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        System.err.println("Failed to close FileOutputStream: " + e.getMessage());
                    }
                }
            }
        }

        // Example usage




    /**
     * 拷贝文件的一部分，从0字节到指定的n字节。
     *
     * @param sourceFilePath 源文件路径
     * @param destFilePath   目标文件路径
     * @param n              需要拷贝的字节数
     * @throws IOException 如果发生I/O错误
     */
    public static void copyPartOfFile(String sourceFilePath, String destFilePath, long n) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFilePath);
            fos = new FileOutputStream(destFilePath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            long bytesToRead = n;

            while ((bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, bytesToRead))) != -1) {
                fos.write(buffer, 0, bytesRead);
                bytesToRead -= bytesRead;
                if (bytesToRead <= 0) {
                    break;
                }
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
