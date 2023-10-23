package com.hss.utilsenhance;

import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.LogUtils;


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
        //registerActivityLifecycleCallbacks(new PlayDecorviewActivityLifeycleCallback());

    }
}
