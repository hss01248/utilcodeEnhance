package com.hss01248.usb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.ArrayList;
import java.util.List;


/**
 * @Despciption todo
 * @Author hss
 * @Date 07/11/2022 10:41
 * @Version 1.0
 */
public class UsbInit implements Initializer<String> {


    @NonNull
    @Override
    public String create(@NonNull Context context) {
        UsbUtil.registerUSBReceiver();
        return "usb";
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
