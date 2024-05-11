package com.hss01248.crash;

import android.app.Application;
import android.content.Context;

import androidx.startup.Initializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 09/05/2022 14:41
 * @Version 1.0
 */
public class CrashInit implements Initializer<String> {
    @Override
    public String create(Context context) {
        if(context instanceof Application){
            Application application = (Application) context;
            TheCrashHandler.getInstance().init(application);

        }
        return "CrashInit";
    }


    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
