package com.hss01248.sentry;

import android.app.Application;
import android.content.Context;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;


import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/10/2023 19:13
 * @Version 1.0
 */
public class SentryInit implements Initializer<String> {
    @NonNull
    @Override
    public String create(@NonNull Context context) {
        Application application = null;
        if(context instanceof Application){
            application = (Application) context;
        }else {
            application = Utils.getApp();
        }
        Application finalApplication = application;
        //SocketTimeoutException: failed to connect to o4505345937768448.ingest.sentry.io/34.120.195.249 (port 443) from /10.12.19.111 (port 40206) after 5000ms

        //new HandlerThread("").start();
        /*ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                SentryUtil.init(finalApplication, AppUtils.isAppDebug());
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });*/

        return "sentry init";
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
