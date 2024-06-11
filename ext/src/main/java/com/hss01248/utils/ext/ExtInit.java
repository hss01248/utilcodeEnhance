package com.hss01248.utils.ext;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import com.blankj.utilcode.util.Utils;
import com.hss01248.utils.ext.lifecycle.BackgroundAndFirstActivityCreatedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/10/2023 19:13
 * @Version 1.0
 */
public class ExtInit implements Initializer<String> {
    @NonNull
    @Override
    public String create(@NonNull Context context) {
        Application application = null;
        if(context instanceof Application){
            application = (Application) context;
        }else {
            application = Utils.getApp();
        }
        application.registerActivityLifecycleCallbacks(new BackgroundAndFirstActivityCreatedCallback());
        return "ext init";
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
