package com.hss01248.sentry.api;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Despciption todo
 * @Author hss
 * @Date 15/04/2022 11:45
 * @Version 1.0
 */
public class ThreadPoolManager {



    public static void runOnBack(Runnable runnable){
        try {
            getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    }catch (Throwable throwable){
                        if(ReporterContainer.isDebug()){
                            throwable.printStackTrace();
                        }
                        ReporterContainer.report(throwable);
                    }

                }
            });
        }catch (Throwable throwable){
            if(ReporterContainer.isDebug()){
                throwable.printStackTrace();
            }
        }

    }

    private static ExecutorService mThreadPool;

    private ThreadPoolManager() {
        mThreadPool = Executors.newFixedThreadPool(2);
    }

    public static ThreadPoolManager getInstance() {
        return ThreadPoolManagerHolder.THREAD_POOL_MANAGER;
    }

    private static final class ThreadPoolManagerHolder {
        private static final ThreadPoolManager THREAD_POOL_MANAGER = new ThreadPoolManager();
    }

    public void submit(Runnable task) {
        try {
            mThreadPool.execute(task);
            //oom
        }catch (Throwable throwable){
            if(ReporterContainer.isDebug()){
                throwable.printStackTrace();
            }
            //delay:
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ReporterContainer.report(throwable);
                }
            },1500);
        }

    }

    public void shutDown(){
        mThreadPool.shutdown();
    }
}
