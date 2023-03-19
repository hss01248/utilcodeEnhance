package com.hss.utils.enhance.type;

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
public class HostInit implements Initializer<String> {
    @Override
    public String create(Context context) {
        if(context instanceof Application){
            Application application = (Application) context;
            HostType.init(application,"");
        }
        return "HostInit";
    }


    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
