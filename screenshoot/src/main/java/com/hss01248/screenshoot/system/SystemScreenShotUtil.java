package com.hss01248.screenshoot.system;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.screenshoot.R;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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


    // todo 根据屏幕的旋转来旋转bitmap, 然后保存-->不用处理,系统会自动处理好
    //todo 预先设置好裁剪框,然后裁剪
    public static void showBitmapDialog(Bitmap bitmap) {
        if (textView != null) {
            String text = bitmap == null ? "!" : "✓";
            int color = bitmap == null? Color.RED : Color.GREEN;
            textView.setText(text);
            textView.setTextColor(color);
            ThreadUtils.getMainHandler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    textView.setText("⋅");
                    textView.setTextColor(Color.parseColor("#33999999"));
                }
            }, 300);

            ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
                @Override
                public Object doInBackground() throws Throwable {
                    long start = System.currentTimeMillis();
                    saveBitmap(bitmap);
                    LogUtils.d("save cost: ",System.currentTimeMillis() -start);
                    return null;
                }

                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFail(Throwable t) {
                    super.onFail(t);
                }
            });

        }
        if (bitmap == null) {
            //ToastUtils.showShort("bitmap is null");
            //LogUtils.i("shot", "bitmap",bitmap.getWidth(),bitmap.getHeight());
            return;
        }

        LogUtils.i("shot", "bitmap", bitmap.getWidth(), bitmap.getHeight());


        if (ActivityUtils.getTopActivity() != null) {
           /* ImageView imageView = new ImageView(ActivityUtils.getTopActivity());
            imageView.setImageBitmap(bitmap);
            Dialog dialog = new Dialog(ActivityUtils.getTopActivity());
            dialog.setContentView(imageView);
            dialog.show();*/
        } else {
            //ImageView imageView = new ImageView(Utils.getApp());
            //imageView.setImageBitmap(bitmap);
        }


    }

    private static void saveBitmap(Bitmap bitmap) throws Exception {
        //根据预订尺寸裁切
        // 假设bitmap是你的原始图像
        Rect rect = readRect();
        if(rect !=null){
            bitmap = getCroppedBitmap(bitmap,rect);
        }

        Bitmap finalBitmap = bitmap;
        ThreadUtils.getMainHandler().post(new Runnable(

        ) {
            @Override
            public void run() {
                if (ActivityUtils.getTopActivity() != null) {
                    ImageView imageView = new ImageView(ActivityUtils.getTopActivity());
                    imageView.setImageBitmap(finalBitmap);
                    Dialog dialog = new Dialog(ActivityUtils.getTopActivity());
                    dialog.setContentView(imageView);
                    dialog.show();
                }
            }
        });


        //压缩成jpg和png,然后对比大小,保留小的

        //文件名规则:
        String fileName = fileName();


        File dir = new File(Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"screenshot2");
        dir.mkdirs();

        File jpgFile = new File(dir,fileName+".jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(jpgFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);

        File finalFile = jpgFile;
        boolean compaired = false;
        if(compaired){
            File pngFile = new File(dir,fileName+".png");
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


        //将文件写到mediastore
        String path = Environment.DIRECTORY_DCIM+"/my-screenshot" ;
        writeToMediaStore(finalFile,path);
        File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+path+"/"+finalFile.getName());
        if(myFile.exists() && myFile.length() >0){
            LogUtils.i("文件成功另存到mediastore:",myFile.getAbsolutePath());
            finalFile.delete();
        }else{
            LogUtils.i("文件另存到mediastore 失败:",myFile.getAbsolutePath());
        }
    }

    private static void writeToMediaStore(File srcFile,String path) throws Exception{
// 获得ContentResolver对象
        ContentResolver resolver = Utils.getApp().getContentResolver();

        // 设置文件信息到ContentValues对象
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, srcFile.getName());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,
                srcFile.getName().endsWith(".jpg")?"image/jpeg":"image/png");
        // 根据文件类型设置
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);

        // 插入文件到系统MediaStore
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri);
                 InputStream inputStream = new FileInputStream(srcFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
            } catch (IOException e) {
                LogUtils.w(e,srcFile.getAbsolutePath());
                throw e;
            }
        } else {
            throw new IOException("Failed to create new MediaStore record: "+srcFile.getAbsolutePath());
        }
    }

    private static String fileName() {
        // 获取当前的日期和时间
        Date now = new Date();
        // 创建SimpleDateFormat对象，定义日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        // 格式化当前日期和时间
        String formattedDate = sdf.format(now);
        return formattedDate;
    }


    static TextView textView;

    public static void createFloatView() {
        EasyFloat.with(ActivityUtils.getTopActivity())
                .setLayout(R.layout.view_float_crop)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                //.setGravity(Gravity.BOTTOM|Gravity.RIGHT,0,-50)
                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                //.setLocation(ScreenUtils.getScreenWidth()- SizeUtils.dp2px(50), ScreenUtils.getScreenHeight()- SizeUtils.dp2px(40))
                // 设置拖拽边界值
                //.setBorder(0, 0,ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight())
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

    public static  void saveRect(Rect rect){
        //[281,599][683,930]
        LogUtils.d(rect.toString(),rect.toShortString());
        String str = "";
        str += rect.left;
        str += ",";
        str += rect.top;
        str += ",";
        str += rect.right;
        str += ",";
        str += rect.bottom;
        LogUtils.i("savedRect",str );
        SPStaticUtils.put("savedRect",str);
    }

    public static  Rect readRect(){
        String string = SPStaticUtils.getString("savedRect");
        if(TextUtils.isEmpty(string)){
            return  null;
        }
        String[] parts = string.split(",");
        Rect rect =   new Rect(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        LogUtils.i("read rect",rect.toString());
        return rect;
    }


    public static Bitmap cropBitmap(Bitmap source, Rect scaledRect, int displayWidth) {
        int originalWidth = source.getWidth();
        int originalHeight = source.getHeight();
        int displayHeight = calculateDisplayHeight(originalWidth, originalHeight, displayWidth);

        float widthRatio = (float) originalWidth / displayWidth;
        float heightRatio = (float) originalHeight / displayHeight;

        int left = Math.round(scaledRect.left * widthRatio);
        int top = Math.round(scaledRect.top * heightRatio);
        int right = Math.round(scaledRect.right * widthRatio);
        int bottom = Math.round(scaledRect.bottom * heightRatio);

        int width = right - left;
        int height = bottom - top;

        return Bitmap.createBitmap(source, left, top, width, height);
    }

    public static int calculateDisplayHeight(int originalWidth, int originalHeight, int displayWidth) {
        return (int) ((float) originalHeight / originalWidth * displayWidth);
    }

    public static Bitmap getCroppedBitmap(Bitmap source, Rect displayRect) {
        int displayWidth = ScreenUtils.getScreenWidth();
        return cropBitmap(source, displayRect, displayWidth);
    }
}
