package com.hss.utils.enhance.timeout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.TimeOutUtil;
import com.hss.utils.enhance.lifecycle.LifecledCallbackHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: Administrator
 * @date: 2022/8/19
 * @desc: //todo
 */
public class TimeoutProxy {


    public static <T> T getProxy(T obj, long timeoutMills, @Nullable Object lifecycledObject,Runnable timeoutRunnable){
        boolean[] hasCallbacked = new boolean[]{false};
        if(timeoutMills>0){
            TimeOutUtil.checkTimeout(hasCallbacked,timeoutMills,lifecycledObject,timeoutRunnable);
        }
        LifecledCallbackHelper lifecledCallbackHelper = new LifecledCallbackHelper(lifecycledObject);

       return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler() {
           @Override
           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if(hasCallbacked[0]){
                        LogUtils.w("TimeoutProxy", "hasCallbacked");
                        return null;
                    }
               if(lifecledCallbackHelper.hasDestoryed()){
                   LogUtils.w("TimeoutProxy", "onFail-canceled by lifecycled object");
                   //onCancel();
                   return null;
               }
               return method.invoke(obj,args);
           }
       });
    }
}
