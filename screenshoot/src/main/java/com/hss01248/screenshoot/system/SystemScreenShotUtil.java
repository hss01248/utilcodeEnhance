package com.hss01248.screenshoot.system;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.screenshoot.R;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;

/**
 * @Despciption 申请权限: 后台弹出界面,显示悬浮窗/system alert window. 长截屏->无障碍服务
 * @Author hss
 * @Date 4/22/24 11:00 AM
 * @Version 1.0
 */
public class SystemScreenShotUtil {
    static final String SCREEN_CAPTURE_CHANNEL_ID = "Screen Capture ID";
    static final String SCREEN_CAPTURE_CHANNEL_NAME = "Screen Capture";
    private static final int SCREEN_SHOT_CODE = 89;

    static boolean created = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createScreenCaptureNotificationChannel() {
        if (created) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        // Create the channel for the notification
        NotificationChannel screenCaptureChannel = new NotificationChannel(SCREEN_CAPTURE_CHANNEL_ID, SCREEN_CAPTURE_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        // Set the Notification Channel for the Notification Manager.
        notificationManager.createNotificationChannel(screenCaptureChannel);
        created = true;
    }


    public static void showBitmapDialog(Bitmap bitmap) {
        if (textView != null) {
            String text = bitmap == null ? "!" : "ok";
            int color = bitmap == null? Color.RED : Color.GREEN;
            textView.setText(text);
            textView.setTextColor(color);
            ThreadUtils.getMainHandler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    textView.setText("X");
                    textView.setTextColor(Color.parseColor("#6d999999"));
                }
            }, 300);
        }
        if (bitmap == null) {
            //ToastUtils.showShort("bitmap is null");
            //LogUtils.i("shot", "bitmap",bitmap.getWidth(),bitmap.getHeight());
            return;
        }

        LogUtils.i("shot", "bitmap", bitmap.getWidth(), bitmap.getHeight());

        if (ActivityUtils.getTopActivity() != null) {
            ImageView imageView = new ImageView(ActivityUtils.getTopActivity());
            imageView.setImageBitmap(bitmap);
            Dialog dialog = new Dialog(ActivityUtils.getTopActivity());
            dialog.setContentView(imageView);
            dialog.show();
        } else {
            ImageView imageView = new ImageView(Utils.getApp());
            imageView.setImageBitmap(bitmap);
            EasyFloat.with(Utils.getApp())
                    .setLayout(imageView)
                    .setTag("image")
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .registerCallbacks(new OnFloatCallbacks() {
                        @Override
                        public void createdResult(boolean b, String s, View view) {

                        }

                        @Override
                        public void show(View view) {

                        }

                        @Override
                        public void hide(View view) {

                        }

                        @Override
                        public void dismiss() {

                        }

                        @Override
                        public void touchEvent(View view, MotionEvent motionEvent) {
                            //EasyFloat.dismiss("image");
                        }

                        @Override
                        public void drag(View view, MotionEvent motionEvent) {

                        }

                        @Override
                        public void dragEnd(View view) {

                        }
                    })
                    .show();
        }


    }


    static TextView textView;

    public static void createFloatView() {
        EasyFloat.with(ActivityUtils.getTopActivity())
                .setLayout(R.layout.view_float_crop)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                //.setLocation(100, 200)
                // 设置拖拽边界值
                .setBorder(0, 0,ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight())
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, String s, View view) {

                    }

                    @Override
                    public void show(View view) {

                        LogUtils.d("on view show: ", view);
                        if (view instanceof TextView) {
                            textView = (TextView) view;
                        }

                    }

                    @Override
                    public void hide(View view) {

                    }

                    @Override
                    public void dismiss() {

                    }

                    long downTime = 0;

                    @Override
                    public void touchEvent(View view, MotionEvent motionEvent) {

                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            if (System.currentTimeMillis() - downTime < 1500) {
                                startCapture();
                            } else {
                                LogUtils.i("大于1.5s,不是点击");
                            }

                        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            downTime = System.currentTimeMillis();
                        }
                    }

                    @Override
                    public void drag(View view, MotionEvent motionEvent) {

                    }

                    @Override
                    public void dragEnd(View view) {

                    }
                })
                .show();
    }


    public static void startCapture() {
        if (CaptureService.mediaProjection != null) {
            Intent service = new Intent(ActivityUtils.getTopActivity(), CaptureService.class);
            //service.putExtra("code", resultCode);
            // service.putExtra("data", data);
            service.putExtra("width", ScreenUtils.getScreenWidth());
            service.putExtra("height", ScreenUtils.getScreenHeight());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SystemScreenShotUtil.createScreenCaptureNotificationChannel();
                ActivityUtils.getTopActivity().startForegroundService(service);
            }
            return;
        }
        //第一步.调起系统捕获屏幕的Intent
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) Utils.getApp().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();

        StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), captureIntent,
                new ActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//第二步通过startForegroundService来获取mediaProjection
                        Intent service = new Intent(ActivityUtils.getTopActivity(), CaptureService.class);
                        service.putExtra("code", resultCode);
                        service.putExtra("data", data);
                        service.putExtra("width", ScreenUtils.getScreenWidth());
                        service.putExtra("height", ScreenUtils.getScreenHeight());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            SystemScreenShotUtil.createScreenCaptureNotificationChannel();
                            ActivityUtils.getTopActivity().startForegroundService(service);
                        }

                    }

                    @Override
                    public void onActivityNotFound(Throwable e) {

                    }
                });

        //ActivityUtils.getTopActivity().startActivityForResult(captureIntent, SCREEN_SHOT_CODE);
    }


}
