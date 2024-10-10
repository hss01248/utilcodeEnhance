package com.hss01248.bigimageviewpager.motion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/10/24 4:44 PM
 * @Version 1.0
 */
public class MotionVideoUtil {



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static  List<byte[]> extratFrames(String videoFilePath) throws Throwable{
        // 1. 创建MediaExtractor对象
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(videoFilePath);
        List<byte[]> byteList = new ArrayList<>();
// 2. 找到视频轨道
        int trackIndex = -1;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                trackIndex = i;
                break;
            }
        }

// 3. 创建MediaCodec对象
        MediaFormat format = extractor.getTrackFormat(trackIndex);
        String mime = format.getString(MediaFormat.KEY_MIME);
        MediaCodec codec = MediaCodec.createDecoderByType(mime);
        codec.configure(format, null, null, 0);

// 4. 开始解码过程
        codec.start();
        ByteBuffer[] inputBuffers = codec.getInputBuffers();
        ByteBuffer[] outputBuffers = codec.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEOS = false;
        long startMs = System.currentTimeMillis();

// ...
        int frameIndex = 0;
        while (!Thread.interrupted()) {
            if (!isEOS) {
                int inIndex = codec.dequeueInputBuffer(10000);
                if (inIndex >= 0) {
                    ByteBuffer buffer = inputBuffers[inIndex];
                    int sampleSize = extractor.readSampleData(buffer, 0);
                    if (sampleSize < 0) {
                        // End of Stream
                        codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                    } else {
                        // Check if the current frame is a key frame
                        if ((extractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                            // This is a key frame, save it as a bitmap
                            byte[] bytes = transToBitmap(format, buffer);
                            byteList.add(bytes);
                            frameIndex++;
                        }
                        codec.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), extractor.getSampleFlags());
                        extractor.advance();
                    }
                }
            }
            // ...
        }


        codec.stop();
        codec.release();
        extractor.release();
        LogUtils.d("frame size", byteList.size());
        return  byteList;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  static  byte[] transToBitmap(MediaFormat format,ByteBuffer buffer) {
        // 获取视频的宽度和高度
        int width = format.getInteger(MediaFormat.KEY_WIDTH);
        int height = format.getInteger(MediaFormat.KEY_HEIGHT);

// 从ByteBuffer中获取YUV格式的图像数据
        byte[] yuvData = new byte[buffer.remaining()];
        buffer.get(yuvData);

// 创建一个YuvImage对象
        YuvImage yuvImage = new YuvImage(yuvData, ImageFormat.NV21, width, height, null);

// 将YuvImage对象转换为JPEG格式的字节数组
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        byte[] imageBytes = out.toByteArray();

// 使用BitmapFactory将字节数组转换为Bitmap对象
       // Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return imageBytes;
    }

    private static int selectVideoTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) { // 音频的话是audio
                extractor.selectTrack(i);
                return i;
            }
        }
        return -1; // 没有找到视频轨道
    }

    private void saveKeyFrameAsImage(ByteBuffer buffer,
                                     int width, int height, String outputPath) {
        // YUV格式的数据
        byte[] yuv = new byte[buffer.capacity()];
        buffer.get(yuv);

        // 将YUV格式的数据转换为RGB格式的Bitmap
        Bitmap bitmap = YuvToBitmap(yuv, width, height);

        // 保存图片到文件
        try {
            File file = new File(outputPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream outputStream = new FileOutputStream(file.getPath() + "/frame_" + System.currentTimeMillis() + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap YuvToBitmap(byte[] yuv, int width, int height) {
        // 创建YuvImage，并指定YUV格式为YUV420
        YuvImage yuvImage = new YuvImage(yuv, ImageFormat.NV21, width, height, null);

        // 将YuvImage转换为ByteBuffer
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        byte[] imageBytes = out.toByteArray();

        // 使用ByteBuffer创建Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
    }

    // 旋转Bitmap的方法
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        // 创建一个可变的Bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
