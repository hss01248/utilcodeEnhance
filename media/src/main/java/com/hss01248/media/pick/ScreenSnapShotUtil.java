package com.hss01248.media.pick;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.R;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss.utils.enhance.media.MediaStoreUtil;
import com.hss01248.toast.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by huangshuisheng on 2017/10/26.
 */

public class ScreenSnapShotUtil {







    public static Bitmap screenShoot(Dialog dialog) {
        View decorView = dialog.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        decorView.destroyDrawingCache();
        return bmp;
    }

    /**
     * 截屏并保存到dcim下,并且通知mediacenter,让系统图库能够马上看到这张图
     */
    public static void screenShootAndSave(Dialog dialog,boolean toastResult, MyCommonCallback<String> callback) {
        Window view = dialog.getWindow();
        screenShootAndSave(view != null ? view.getDecorView() : null,toastResult,callback);
    }

    public static void screenShootAndSave(Activity activity,boolean toastResult, MyCommonCallback<String> callback) {
        screenShootAndSave(activity.getWindow().getDecorView(),toastResult,callback);
    }

    public static void screenShootAndSave(View view,boolean toastResult, MyCommonCallback<String> callback) {
        screenShootAndSave(view, 0,toastResult,callback);
    }

    public static void screenShootAndSave(View view, @ColorRes int colorResId,
                                          boolean toastResult, MyCommonCallback<String> callback) {
        if (view == null) {
            return;
        }

        try {
            if (view instanceof ScrollView) {
                Bitmap bmp = getBitmapByView((ScrollView) view, colorResId);
                requestStoragePermissionAndSave(bmp, view,toastResult,callback);
            } else {
                View decorView = view;
                decorView.setDrawingCacheEnabled(true);
                decorView.buildDrawingCache();
                Bitmap bmp = decorView.getDrawingCache();
                requestStoragePermissionAndSave(bmp, decorView,toastResult,callback);
            }
        } catch (Throwable e) {
           LogUtils.w(e);
           if(toastResult)
            ToastUtils.showLong(getString2(R.string.deliver_error_save_failed,view.getContext()));
            if(callback != null)
            callback.onError("Java Exception",e.getMessage(),e);
        }

    }

    private static String getString2(int deliver_error_save_failed, Context context) {
        return context.getResources().getString(deliver_error_save_failed);
    }

    public static void saveBitmap(Bitmap bmp,  boolean showToast, MyCommonCallback<String> callback) {

        try {
            saveImageToGallery2(Utils.getApp(),bmp,showToast,callback);
        } catch (Throwable e) {
            if(showToast){
                MyToast.error(e.getMessage());
            }
        }
    }

    private static void requestStoragePermissionAndSave(Bitmap bmp, View view,  boolean showToast, MyCommonCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmap(bmp,showToast, callback);
            return;
        }
        if(PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            saveBitmap(bmp, showToast, callback);
        }else {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    saveBitmap(bmp, showToast, callback);
                }

                @Override
                public void onDenied() {
                    saveBitmap(bmp,showToast, callback);
                }
            }).request();
        }

    }

    /**
     * 保存图片到文件
     */
    private static boolean saveBitmapToFile(String filename, Bitmap bitmap, int quality,
                                            Bitmap.CompressFormat compressFormat) {
        if (null == bitmap || TextUtils.isEmpty(filename)) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            File file = new File(filename);
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(compressFormat, quality, fos);
            fos.flush();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static File getAlbumDir(Context context) {
        File dir =  null;
        if(PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Screenshots");
        }else {
            dir =  context.getExternalFilesDir("snapshot-"+context.getPackageName());
        }
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    private static Bitmap getBitmapByView(ScrollView scrollView, @ColorRes int colorId) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            View child = scrollView.getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            h += child.getHeight();
            if (colorId != 0) {
                scrollView.getChildAt(i).setBackgroundColor(scrollView.getContext().getResources().getColor(colorId));
            }else {
                scrollView.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }


    /**
     * android 11及以上保存图片到相册
     * @param context
     * @param image
     */
    private static void saveImageToGallery2(Context context, Bitmap image,boolean showToast, MyCommonCallback<String> callback) throws Throwable{





        Long mImageTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(mImageTime));
        String mImageFileName = AppUtils.getAppName().toLowerCase()+"-"+imageDate+ ".jpg";//图片名称
        String realPath = Environment.DIRECTORY_PICTURES + File.separator + "Screenshots";

        File file = new File(Utils.getApp().getExternalCacheDir(),mImageFileName);
        file.createNewFile();
        FileOutputStream inputStream = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.JPEG,85,inputStream);
        MediaStoreUtil.writeMediaToMediaStore(file, realPath, new MyCommonCallback3<String>() {
            @Override
            public void onSuccess(String uri) {
                callback.onSuccess(uri.toString());

            }

            @Override
            public void onError(String code, String msg, @Nullable Throwable throwable) {
                MyCommonCallback3.super.onError(code, msg, throwable);
                callback.onError(code, msg, throwable);
                if(showToast){
                    MyToast.error(msg);
                }
            }
        });
    }


    public static Bitmap mixTransInPng(Bitmap tagBitmap){
        //原bitmap是imutable,不能直接更改像素点,要新建bitmap,像素编辑后设置
        boolean isPngWithTransAlpha = hasTransInAlpha(tagBitmap);
        if(!isPngWithTransAlpha){
            return tagBitmap;
        }
        int w = tagBitmap.getWidth();
        int h = tagBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        int tintBgColorIfHasTransInAlpha = 0x00ffffff;
        long start = System.currentTimeMillis();
        out:
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // The argb {@link Color} at the specified coordinate
                int pix = tagBitmap.getPixel(i, j);
                long alpha = ((pix >> 24) & 0xff);/// 255.0f
                // Log.d("luban","位置:"+i+"-"+j+", 不透明度:"+a);
                //255就是没有透明度, = 0 就是完全透明. 值代表不透明度.值越大,越不透明
                if (alpha != 255) {
                    //半透明时,白色化,而不是像Android原生内部实现一样简单粗暴地将不透明度设置为0,一片黑色

                    //策略1: 不混合颜色,只区分0和255.只要有半透明,就使用前景色  性能还可以
                           /* if(alpha == 0){
                                tintBgColorIfHasTransInAlpha = tintBgColorIfHasTransInAlpha | 0xff000000;
                                pix = tintBgColorIfHasTransInAlpha ;
                                //也可以改成外部传入背景色
                            }else {
                               pix = pix | 0xff000000;
                            }*/

                    //策略2: 颜色混合:  显示颜色= 前景色* alpha/255 + 背景色 * (255 - alpha)/255.
                    if (alpha == 0) {
                        //将alpha改成255.完全不透明
                        pix = tintBgColorIfHasTransInAlpha | 0xff000000;
                    } else {
                               /* 要使用rgb三个通道分别计算,而不能作为一个int值整体计算:
                               long pix2 = (long) (pix * alpha/255f +  tintBgColorIfHasTransInAlpha * (255f-alpha) / 255f);
                                pix2 = pix2 | 0xff000000; */

                        int r = ((pix >> 16) & 0xff);
                        int g = ((pix >> 8) & 0xff);
                        int b = ((pix) & 0xff);

                        int br = ((tintBgColorIfHasTransInAlpha >> 16) & 0xff);
                        int bg = ((tintBgColorIfHasTransInAlpha >> 8) & 0xff);
                        int bb = ((tintBgColorIfHasTransInAlpha) & 0xff);

                        int fr = Math.round((r * alpha + br * (255 - alpha)) / 255f);
                        int fg = Math.round((g * alpha + bg * (255 - alpha)) / 255f);
                        int fb = Math.round((b * alpha + bb * (255 - alpha)) / 255f);

                        // 注意是用或,不是用加: pix = 0xff << 24 + fr << 16 + fg << 8 + fb;
                        pix = (0xff << 24) | (fr << 16) | (fg << 8) | fb;
                        //等效: Color.argb(0xff,fr,fg,fb);
                    }
                    bitmap.setPixel(i, j, pix);
                } else {
                    bitmap.setPixel(i, j, pix);
                }
            }
        }
        LogUtils.d("半透明通道颜色混合 cost(ms):" , (System.currentTimeMillis() - start));
        return bitmap;
    }


    public static boolean hasTransInAlpha(Bitmap bitmap) {
        if (!bitmap.getConfig().equals(Bitmap.Config.ARGB_8888)) {
            return false;
        }
        int w = bitmap.getWidth() - 1;
        int h = bitmap.getHeight() - 1;
        if (isTrans(bitmap, 0, 0)
                || isTrans(bitmap, w, h)
                || isTrans(bitmap, 0, h)
                || isTrans(bitmap, w, 0)
                || isTrans(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2)) {
            //先判断4个顶点和中心.
            return true;
        }
        //然后折半查找
        return hasTransInAngel(bitmap, w, h);
    }

    private static boolean hasTransInAngel(Bitmap bitmap, int w, int h) {
        Log.d("ss", "hastrans: porint:" + w + "-" + h);
        // int[][] arr = new int[8][2];
        if (w == 0 || h == 0) {
            return false;
        }
        int halfw = w / 2;
        int halfh = h / 2;

        boolean hasTrans = isTrans(bitmap, w, h)
                || isTrans(bitmap, w, 0)
                || isTrans(bitmap, 0, h)
                || isTrans(bitmap, w, halfh)
                || isTrans(bitmap, halfw, h)
                || isTrans(bitmap, 0, halfh)
                || isTrans(bitmap, halfw, 0);
        if (hasTrans) {
            return hasTrans;
        }
        return hasTransInAngel(bitmap, halfw, halfh);
    }

    private static boolean isTrans(Bitmap bitmap, int x, int y) {
        int pix = bitmap.getPixel(x, y);
        int a = ((pix >> 24) & 0xff);
        return a != 255;
    }


}
