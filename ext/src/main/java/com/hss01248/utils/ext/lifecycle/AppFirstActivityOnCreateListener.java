package com.hss01248.utils.ext.lifecycle;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * 一些必须初始化调用,但又会发送网络请求的,可以将网络请求拆分,放到此处
 */
public interface AppFirstActivityOnCreateListener {

    /**
     * 不用担心崩溃,有catch和上报
     * @param activity
     * @param savedInstanceState
     *  从桌面启动:from_launcher 其他: 按activity.getReferrer()本身的规则  示例:
     *                 //android-app://com.huawei.android.launcher launcher点击图标启动--> 归类到from_launcher
     *                  //android-app://com.android.shell  - android studio adb启动
     *                   //android-app://com.android.packageinstaller
     */
    default void onFirtActivityCreated(Activity activity,@Nullable Bundle savedInstanceState){

    }

    default void  onForegroundBackgroundChanged(Activity activity,boolean changeToBackground){

    }

    default int order(){
        return 0;
    }

}
