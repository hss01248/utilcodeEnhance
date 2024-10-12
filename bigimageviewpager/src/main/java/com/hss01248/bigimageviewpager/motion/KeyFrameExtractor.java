package com.hss01248.bigimageviewpager.motion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.api.MyCommonCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class KeyFrameExtractor {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<File> extractKeyFrames(Context context, String videoPath) throws IOException {
        File videoFile = new File(videoPath);
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(videoFile.getAbsolutePath());
        int trackIndex = selectVideoTrack(extractor);
        List<File> files = new ArrayList<>();
        if (trackIndex < 0) {
            throw new RuntimeException("No video track found in file");
        }

        extractor.selectTrack(trackIndex);
        MediaFormat format = extractor.getTrackFormat(trackIndex);
        String mime = format.getString(MediaFormat.KEY_MIME);
        MediaCodec codec = MediaCodec.createDecoderByType(mime);

        ImageReader imageReader = ImageReader.newInstance(format.getInteger(MediaFormat.KEY_WIDTH),
                format.getInteger(MediaFormat.KEY_HEIGHT), ImageFormat.JPEG, 4);
        codec.configure(format, imageReader.getSurface(), null, 0);
        codec.start();

        File outputDir = new File(context.getExternalCacheDir(), videoFile.getName().replaceFirst("[.][^.]+$", ""));
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        boolean isEOS = false;
        long timeoutUs = 10000;

        while (!isEOS) {
            int inIndex = codec.dequeueInputBuffer(timeoutUs);
            if (inIndex >= 0) {
                ByteBuffer buffer = codec.getInputBuffer(inIndex);
                int sampleSize = extractor.readSampleData(buffer, 0);
                if (sampleSize < 0) {
                    codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isEOS = true;
                } else {
                    boolean isKeyFrame = (extractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0;
                    LogUtils.d("isKeyFrame: "+isKeyFrame,"inIndex:"+inIndex,"sampleSize:"+sampleSize);
                    if (isKeyFrame) {
                        codec.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                        int outIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs);
                        LogUtils.d("--->isKeyFrame: "+isKeyFrame,"inIndex:"+inIndex,"sampleSize:"+sampleSize,"outIndex:"+outIndex);
                        if (outIndex >= 0) {
                            Image image = imageReader.acquireNextImage();
                            ByteBuffer outputBuffer = image.getPlanes()[0].getBuffer();
                            Bitmap bitmap = Bitmap.createBitmap(format.getInteger(MediaFormat.KEY_WIDTH), format.getInteger(MediaFormat.KEY_HEIGHT), Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(outputBuffer);
                            File file = new File(outputDir, "frame_" + extractor.getSampleTime() + ".jpg");
                            LogUtils.d("save frames to file: ",file.getAbsolutePath());
                            saveBitmap(bitmap, file);
                            files.add(file);
                            image.close();
                            codec.releaseOutputBuffer(outIndex, true);
                        }
                    }
                    extractor.advance();
                }
            }
        }
        codec.stop();
        codec.release();
        extractor.release();
        return files;
    }

    private static int selectVideoTrack(MediaExtractor extractor) {
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                return i;
            }
        }
        return -1;
    }

    private static void saveBitmap(Bitmap bitmap, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();
        bitmap.recycle();
    }





    public static List<byte[]> extractFrames(String videoPath) {
        MediaExtractor extractor = new MediaExtractor();
        MediaCodec codec = null;
        List<byte[]> bytes = new ArrayList<>();
        try {
            extractor.setDataSource(videoPath);
            MediaFormat format = extractor.getTrackFormat(0);
            extractor.selectTrack(0);

             codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
            codec.configure(format, null, null, 0);
            codec.start();

            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();

            boolean isEOS = false;
            while (!isEOS) {
                int inputBufferIndex = codec.dequeueInputBuffer(10000);
                if (inputBufferIndex >= 0) {
                    // Read data from extractor and feed it to decoder
                    int sampleSize = extractor.readSampleData(inputBuffers[inputBufferIndex], 0);
                    long presentationTimeUs = extractor.getSampleTime();
                    if (sampleSize < 0) {
                        codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                    } else {
                        codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
                        extractor.advance();
                    }
                }

                // Dequeue the output buffer and process the frame
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    // Process the frame here (for example, convert to a Bitmap)
                    byte[] bytett = processFrame(outputBuffer, bufferInfo, format);
                    if(bytett.length >0){
                        bytes.add(bytett);
                    }

                    codec.releaseOutputBuffer(outputBufferIndex, false);
                }
            }
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            extractor.release();
            codec.stop();
            codec.release();
        }
    }

   // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  static byte[] processFrame(ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo,MediaFormat format) {
        // Conversion and processing logic goes here
        byte[] bytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            try {
                bytes = MotionVideoUtil.transToBitmap( format,outputBuffer);
            }catch (Throwable throwable){
                LogUtils.w(throwable);
            }

        }
        return bytes;
    }

    public static List<byte[]> extractFrames2(String motionVideoPath, int totalThumbsCount, MyCommonCallback<byte[]> eachCallback) throws Exception{
        List<byte[]> bytes = new ArrayList<>();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(motionVideoPath);
        long duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        // Retrieve media data use microsecond
        long interval = duration / (totalThumbsCount - 1);
        for (long i = 0; i < totalThumbsCount; ++i) {
            long frameTime =  interval * i;
            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime*1000 , MediaMetadataRetriever.OPTION_CLOSEST);
            if(bitmap ==null){
                continue;
            }
            //OPTION_CLOSEST：获取最接近指定时间点的帧，无论该帧是否为关键帧。这是默认选项。
            //MediaMetadataRetriever.OPTION_CLOSEST_SYNC 最近的关键帧
            //OPTION_PREVIOUS_SYNC：从指定时间点开始，获取上一个关键帧
            //OPTION_NEXT_SYNC：从指定时间点开始，获取下一个关键帧。
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,95,out);
            bitmap.recycle();
            byte[] imageBytes = out.toByteArray();
            if(bytes.isEmpty()){
                bytes.add(imageBytes);
                eachCallback.onSuccess(imageBytes);
            }else {
                if(bytes.get(bytes.size()-1).length != imageBytes.length){
                    bytes.add(imageBytes);
                    eachCallback.onSuccess(imageBytes);
                }else {
                    LogUtils.d("同一个关键帧: "+i);
                }
            }
            out.close();
        }
        mediaMetadataRetriever.release();
        return bytes;
    }
}

