package com.hss01248.screenshoot.system;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/7/24 9:12 AM
 * @Version 1.0
 */
public class CaptureService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new NotificationCompat.Builder(this, SystemScreenShotUtil.SCREEN_CAPTURE_CHANNEL_ID).build());
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static MediaProjection mediaProjection;
    int width = 0;
    int height = 0;
    int retryCount = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int resultCode = intent.getIntExtra("code", -1);
        Intent data = intent.getParcelableExtra("data");
        width = intent.getIntExtra("width",1080);
        height = intent.getIntExtra("height",1920);
        boolean onlyPermission = intent.getBooleanExtra("onlyPermission",false);
        //service.putExtra("onlyPermission", onlyPermission);


        //第三步：获取mediaProjection
        if(mediaProjection ==null){
            MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        }

        if (mediaProjection == null) {
            LogUtils.e("shot", "media projection is null");
            return super.onStartCommand(intent, flags, startId);
        }
        if(onlyPermission){
            LogUtils.e("shot", "only permission");
            return super.onStartCommand(intent, flags, startId);
        }
        Bitmap bitmap = screenShot(mediaProjection);
        //重试3次
        if(bitmap ==null){
            LogUtils.w("retry capture1");
            bitmap = screenShot(mediaProjection);
        }
        if(bitmap ==null){
            LogUtils.w("retry capture2");
            bitmap = screenShot(mediaProjection);
        }
        if(bitmap ==null){
            LogUtils.w("retry capture3");
            bitmap = screenShot(mediaProjection);
        }

        SystemScreenShotUtil.showBitmapDialog(bitmap);


        return super.onStartCommand(intent, flags, startId);

    }

    public Bitmap screenShot(MediaProjection mediaProjection){
        long startTime0 = System.currentTimeMillis();
        Objects.requireNonNull(mediaProjection);
        @SuppressLint("WrongConstant")
        ImageReader imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3);
        VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay("screen", width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,imageReader.getSurface(), null, null);
        //取现在最新的图片
        //SystemClock.sleep(1000);
        //delay needed because getMediaProjection() throws an error if it's called too soon
        SystemClock.sleep(100);
        //取最新的图片
        Image image = imageReader.acquireLatestImage();
//        Image image = imageReader.acquireNextImage();
        //释放 virtualDisplay,不释放会报错
        virtualDisplay.release();

        LogUtils.d("acquireLatestImage cost ms ",System.currentTimeMillis() - startTime0);
        long startTime = System.currentTimeMillis();
        Bitmap bitmap =  image2Bitmap(image);
        LogUtils.d("image2Bitmap cost ms ",System.currentTimeMillis() - startTime);
        return bitmap;
    }

    //第五步:将Image转为Bitmap
    public static Bitmap image2Bitmap(Image image) {
        if (image == null) {
            System.out.println("image 为空");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;

        Bitmap bitmap = Bitmap.createBitmap(width+ rowPadding / pixelStride , height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        //截取图片
//        Bitmap cutBitmap = Bitmap.createBitmap(bitmap,0,0,width/2,height/2);

        //压缩图片
//        Matrix matrix = new Matrix();
//        matrix.setScale(0.5F, 0.5F);
//        System.out.println(bitmap.isMutable());
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        image.close();
        return bitmap;
    }
}
