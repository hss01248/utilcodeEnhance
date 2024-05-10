package com.hss01248.screenshotapp;

import androidx.multidex.MultiDexApplication;

import com.hss01248.glidev4.Glide4Loader;
import com.hss01248.image.ImageLoader;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/10/24 5:49 PM
 * @Version 1.0
 */
public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this,250,new Glide4Loader());
    }
}
