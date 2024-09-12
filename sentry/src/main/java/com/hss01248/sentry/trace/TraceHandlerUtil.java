package com.hss01248.sentry.trace;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 03/03/2022 19:36
 * @Version 1.0
 */
public class TraceHandlerUtil {

    static volatile Handler traceHandler;
    static Thread thread;
    public static void init(){
        thread =   new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                traceHandler = new Handler(Looper.myLooper());
                Looper.loop();
            }
        });
        thread.start();
    }

    public static void postDelayed(Runnable runnable,long time){
        try {
            if(traceHandler != null){
                traceHandler.postDelayed(runnable, time);
            }else {
                //刚启动的时间上报,大概率为null
                Log.w("apm","traceHandler is still null");
                runnable.run();
            }
        }catch (Throwable throwable){
            LogUtils.w(throwable);
        }

        //ThreadUtils.getMainHandler().postDelayed(runnable, time);
    }
}
