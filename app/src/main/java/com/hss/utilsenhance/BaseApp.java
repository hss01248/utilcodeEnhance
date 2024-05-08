package com.hss.utilsenhance;

import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.glidev4.Glide4Loader;
import com.hss01248.image.ImageLoader;


/**
 * @Despciption todo
 * @Author hss
 * @Date 24/02/2022 10:09
 * @Version 1.0
 */
public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.getConfig().setLogSwitch(true);
        ImageLoader.init(this,250,new Glide4Loader());
        LogUtils.getConfig().setOnConsoleOutputListener(new LogUtils.OnConsoleOutputListener() {
            @Override
            public void onConsoleOutput(int type, String tag, String content) {

            }
        });
        //registerActivityLifecycleCallbacks(new PlayDecorviewActivityLifeycleCallback());

    }
}
