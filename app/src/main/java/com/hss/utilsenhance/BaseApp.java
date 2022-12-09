package com.hss.utilsenhance;

import androidx.multidex.MultiDexApplication;




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

        registerActivityLifecycleCallbacks(new PlayDecorviewActivityLifeycleCallback());

    }
}
