package com.hss01248.app_motion_photo;

import androidx.multidex.MultiDexApplication;

import com.hss01248.fileoperation.FileDeleteUtil;
import com.hss01248.img.compressor.ImageCompressor;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/16/24 4:00 PM
 * @Version 1.0
 */
public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDeleteUtil.askMediaManagerPermission = false;
        ImageCompressor.doNotCompressMotionPhoto = false;
    }
}
