package com.hss.utils.enhance.timeout;

import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.TimeOutUtil;
import com.hss.utils.enhance.lifecycle.LifecledCallbackHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: Administrator
 * @date: 2022/8/19
 * @desc: //todo
 */
public class TimeoutAndLifeCycleProxy {


    public static <T> T getProxy(T obj, long timeoutMills, @Nullable Object lifecycledObject,Runnable timeoutRunnable){

        Class<?>[] classes = getInterfaces2(obj);

        if (classes == null || classes.length == 0) {
            Log.w("TimeoutProxy", obj.getClass().getName() + " : no interfaces");
            return obj;
        }
        LifecledCallbackHelper lifecledCallbackHelper = new LifecledCallbackHelper(lifecycledObject);
        TimeoutHelper timeoutHelper = new TimeoutHelper(timeoutMills,lifecycledObject,timeoutRunnable);
        timeoutHelper.onStart();

       return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), classes, new InvocationHandler() {
           @Override
           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
               if(timeoutHelper.hasTimeout()){
                   LogUtils.w("TimeoutProxy", "hasCallbacked");
                   return null;
               }
               if(lifecledCallbackHelper.hasDestoryed()){
                   LogUtils.w("TimeoutProxy", "onFail-canceled by lifecycled object");
                   //onCancel();
                   return null;
               }
               //method.getAnnotations();
               //todo 根据注解决定是否处理timeout,是否处理线程切换,是否进行try catch,
               timeoutHelper.setShouldNotInvokeTimeoutCallbackWhenTimeout();
               return method.invoke(obj,args);
           }
       });
    }


    private static <T> Class<?>[] getInterfaces2(T impl) {
        Set<Class> classes = getInterfaces(impl.getClass(), null);
        if (classes.size() > 0) {
            Class[] classes1 = new Class[classes.size()];
            Iterator<Class> iterator = classes.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                classes1[i] = iterator.next();
                i++;
            }
            return classes1;
        }
        return null;
    }
   
    private static <T> Set<Class> getInterfaces(Class clazz, Set<Class> classes) {
        if (classes == null) {
            classes = new HashSet<>();
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces == null || interfaces.length == 0) {

        } else {
            for (Class<?> anInterface : interfaces) {
                classes.add(anInterface);
                //接口的父类
                Class superInter = anInterface.getSuperclass();
                if (superInter != null) {
                    classes.addAll(getInterfaces(superInter, classes));
                }
            }
        }
        //类的父类
        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            classes.addAll(getInterfaces(superClazz, classes));
        }
        return classes;
    }
}
