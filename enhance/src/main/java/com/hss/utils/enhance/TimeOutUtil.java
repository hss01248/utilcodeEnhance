package com.hss.utils.enhance;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.lifecycle.SimpleLifeCycledTask;

import java.util.concurrent.TimeUnit;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/08/2022 10:51
 * @Version 1.0
 */
public class TimeOutUtil {

    /**
     *
     * @param hasCallbacked boolean[]{false},必须声明在栈中,才能保持线程安全. 正常任务执行后,应该置为true
     * @param timeoutMills
     * @param lifecycledObject activity,context,fragment
     * @param runnableWhenTimeout 回调运行在主线程
     */
    public static  void checkTimeout(boolean[] hasCallbacked, long timeoutMills, @Nullable Object lifecycledObject,@Nullable Runnable runnableWhenTimeout){
        if(hasCallbacked == null){
            throw new RuntimeException("hasCallbacked should not be null");
        }
        if(hasCallbacked.length != 1){
            throw new RuntimeException("hasCallbacked.length should  be 1");
        }
        ThreadUtils.executeByCachedWithDelay(new SimpleLifeCycledTask<Object>(lifecycledObject) {
            @Override
            public Object doInBackground() throws Throwable {
                return null;
            }

            @Override
            protected void onSuccessReally(Object result) {
                if(hasCallbacked[0]){
                    LogUtils.v("timeout","之前成功过,本处超时不处理");
                    return;
                }
                hasCallbacked[0] = true;
                if(runnableWhenTimeout != null){
                    runnableWhenTimeout.run();
                }
            }

        }, timeoutMills, TimeUnit.MILLISECONDS);
    }
}
