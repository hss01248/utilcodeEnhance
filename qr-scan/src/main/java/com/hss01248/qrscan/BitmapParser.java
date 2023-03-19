package com.hss01248.qrscan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.io.FileNotFoundException;

import top.zibin.luban.Luban;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/12/2022 16:49
 * @Version 1.0
 */
public class BitmapParser {


    public static Bitmap parseFromUri(Uri uri) throws Throwable{
        if(uri == null){
            return  null;
        }
        BitmapFactory.Options options  = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        LogUtils.w("uri ", uri);
        if("file".equals(uri.getScheme())){
            String path = uri.getPath();
            LogUtils.w("path ", path);
            BitmapFactory.decodeFile(path, options);
        }else {
            BitmapFactory.decodeStream(Utils.getApp().getContentResolver().openInputStream(uri),new Rect(),options);
        }
        //todo 待优化
        options.inSampleSize = Luban.computeInSampleSize(options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        if("file".equals(uri.getScheme())){
            String path = uri.getPath();
          return   BitmapFactory.decodeFile(path, options);
        }else {
           return BitmapFactory.decodeStream(Utils.getApp().getContentResolver().openInputStream(uri),new Rect(),options);
        }
    }
}
