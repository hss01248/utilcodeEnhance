package com.hss01248.bigimageviewpager.motion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

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
                    if (isKeyFrame) {
                        codec.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                        int outIndex = codec.dequeueOutputBuffer(bufferInfo, timeoutUs);
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

}

