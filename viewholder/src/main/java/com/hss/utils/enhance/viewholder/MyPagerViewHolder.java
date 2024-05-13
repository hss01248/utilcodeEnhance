package com.hss.utils.enhance.viewholder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 10:15 AM
 * @Version 1.0
 */
public abstract class MyPagerViewHolder<VB extends ViewBinding, T> implements DefaultLifecycleObserver {


    public View rootView;
    public MyPagerViewHolder(Context context) {
        doInit(context);
    }

    /**
     * 忽略了很多判空,为空就不给用了,直接崩溃
     * @param context
     */
    private void doInit(Context context) {
        Activity activity = LifecycleObjectUtil.getActivityFromContext(context);
        if(activity ==null){
            LogUtils.w("");
            activity= ActivityUtils.getTopActivity();
        }
        LifecycleObjectUtil.getLifecycleOwnerFromObj(context).getLifecycle().addObserver(this);
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        LayoutInflater inflater =  LayoutInflater.from(context);
        VB viewBinding = initViewBinding(inflater,activity.findViewById(android.R.id.content));
        rootView = viewBinding.getRoot();
    }

    protected  VB initViewBinding(LayoutInflater inflater, ViewGroup parent){
        try {
            Class<VB> clazz = ((Class<VB>) ((ParameterizedType) (this.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0]);
            Method method = clazz.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            method.setAccessible(true);
            VB vb = (VB) method.invoke(clazz,inflater,parent,false);
            return vb;
        }catch (Throwable e){
            LogUtils.w(e);
            return null;
        }
    }


    public  abstract void assingDatasAndEvents(Context context, List datas, T data, int position, MyPagerAdapter dataAdapter);




}
