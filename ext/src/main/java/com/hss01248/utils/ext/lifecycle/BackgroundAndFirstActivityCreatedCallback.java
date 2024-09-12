package com.hss01248.utils.ext.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * @Despciption todo
 * @Author hss
 * @Date 20/06/2022 10:29
 * @Version 1.0
 */
public class BackgroundAndFirstActivityCreatedCallback implements Application.ActivityLifecycleCallbacks {

    /**
     * 冷启动时,或者崩溃重启时,不一定走splash.
     * 一些必须初始化调用,但又会发送网络请求的,可以将网络请求拆分,放到此处
     * 不用担心崩溃,有catch和上报
     *
     * @param appFirstActivityOnCreateListener
     */
    public static void addAppFirstActivityOnCreateListener(AppFirstActivityOnCreateListener appFirstActivityOnCreateListener) {
        if(appFirstActivityOnCreateListener != null){
            appFirstActivityOnCreateListeners.add(appFirstActivityOnCreateListener);
            Collections.sort(appFirstActivityOnCreateListeners, new Comparator<AppFirstActivityOnCreateListener>() {
                @Override
                public int compare(AppFirstActivityOnCreateListener o1, AppFirstActivityOnCreateListener o2) {
                    return o2.order() - o1.order();
                }
            });
        }else {
            LogUtils.w(" show not add null listener");
        }

    }

    static List<AppFirstActivityOnCreateListener> appFirstActivityOnCreateListeners = new ArrayList<>();


    boolean hasInit = false;
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(!hasInit){
            hasInit = true;
            try {
                onFirstActivityCreated(activity,savedInstanceState);
            }catch (Throwable throwable){
                LogUtils.w(throwable);
            }

        }
    }

    private void onFirstActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (appFirstActivityOnCreateListeners != null && !appFirstActivityOnCreateListeners.isEmpty()) {
            for (AppFirstActivityOnCreateListener appFirstActivityOnCreateListener : appFirstActivityOnCreateListeners) {
                try {
                    appFirstActivityOnCreateListener.onFirtActivityCreated(activity, savedInstanceState);
                } catch (Throwable throwable) {
                    LogUtils.w(throwable);
                }
            }
        }
    }

    int count = -1;
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        count++;
        if(count ==0){
            onForeBackChanged(activity,false);
        }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        count--;
        if(count ==0){
            onForeBackChanged(activity,true);
        }
    }

    private void onForeBackChanged(Activity activity,boolean changeToBackground) {
        LogUtils.d(activity,"ToBackground:"+changeToBackground);
       /* if(activity.isFinishing()){

        }*/
        if (appFirstActivityOnCreateListeners != null && !appFirstActivityOnCreateListeners.isEmpty()) {
            for (AppFirstActivityOnCreateListener appFirstActivityOnCreateListener : appFirstActivityOnCreateListeners) {
                try {
                    appFirstActivityOnCreateListener.onForegroundBackgroundChanged(activity, changeToBackground);
                } catch (Throwable throwable) {
                    LogUtils.w(throwable);
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
