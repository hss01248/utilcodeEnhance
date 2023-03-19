package com.hss.utils.enhance.timeout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.lifecycle.SimpleLifeCycledTask;

import java.util.concurrent.TimeUnit;

/**
 * @author: Administrator
 * @date: 2022/8/20
 * @desc: //todo
 */
public class TimeoutHelper {

   private long timeoutMills;
    private Object  lifeCycledObj;
    public TimeoutHelper(long timeoutMills,Object  lifeCycledObj, Runnable timeoutRunnable) {
        this.timeoutMills = timeoutMills;
        this.timeoutRunnable = timeoutRunnable;
        this.lifeCycledObj = lifeCycledObj;
    }

    private Runnable timeoutRunnable;

    private volatile boolean hasTimeout;

    public void setShouldNotInvokeTimeoutCallbackWhenTimeout() {
        this.shouldInvokeTimeoutCallbackWhenTimeout = false;
    }

    private volatile boolean shouldInvokeTimeoutCallbackWhenTimeout = true;

    public void onStart(){
        if(timeoutMills<=0){
            return;
        }
        ThreadUtils.executeByCachedWithDelay(new SimpleLifeCycledTask<Object>(lifeCycledObj) {
            @Override
            public Object doInBackground() throws Throwable {
                return null;
            }

            @Override
            protected void onSuccessReally(Object result) {
                if(!shouldInvokeTimeoutCallbackWhenTimeout){
                    LogUtils.v("timeout","之前成功过,本处超时不处理");
                    return;
                }
                hasTimeout = true;
                if(timeoutRunnable != null){
                    timeoutRunnable.run();
                }
            }

        }, timeoutMills, TimeUnit.MILLISECONDS);
    }

    public boolean hasTimeout(){
        if(timeoutMills<=0){
            return false;
        }
        return hasTimeout;
    }



}
