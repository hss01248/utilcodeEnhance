package com.hss.utils.enhance.lifecycle;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.lifecycle.LifecledCallbackHelper;


/**
 * @Despciption todo 使用aop生成代码
 * @Author hss
 * @Date 11/08/2022 10:01
 * @Version 1.0
 */
public abstract class SimpleLifeCycledTask<T> extends ThreadUtils.SimpleTask<T> {


    protected LifecledCallbackHelper lifecledCallbackHelper;

    public SimpleLifeCycledTask(Object lifeCycledObj) {
        lifecledCallbackHelper = new LifecledCallbackHelper(lifeCycledObj);
    }


    @Override
    public void onSuccess(T result) {
        if(lifecledCallbackHelper.hasDestoryed()){
            LogUtils.wTag("ThreadUtils", "onSuccess-canceled by lifecyled object");
            //onCancel();
            return;
        }
        onSuccessReally(result);
    }

    @Override
    public void onFail(Throwable t) {
        if(lifecledCallbackHelper.hasDestoryed()){
            LogUtils.w("ThreadUtils", "onFail-canceled by lifecycled object");
            //onCancel();
            return;
        }
        onFailReally(t);
    }

    protected  void onFailReally(Throwable t){
        LogUtils.w("ThreadUtils", "onFailReally: ", t);
    }

    protected abstract void onSuccessReally(T result);


}
