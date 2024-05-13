package com.hss.utils.enhance.viewholder;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;
import com.hss01248.bus.GenericClassUtil;
import com.hss01248.fullscreendialog.FullScreenDialog;
import com.hss01248.viewstate.StatefulLayout;
import com.hss01248.viewstate.ViewStateConfig;

import java.util.List;

/**
 * @Despciption ViewBinding可以外面传入(include标签部分),也可以内部自己生成
 * @Author hss
 * @Date 22/06/2022 16:27
 * @Version 1.0
 */
@Deprecated
public abstract  class MyViewHolder<VB extends ViewBinding,T> implements DefaultLifecycleObserver {

    public VB binding;
    public T data;

    public StatefulLayout getStatefulLayout() {
        return statefulLayout;
    }

    protected StatefulLayout statefulLayout;


    public MyViewHolder(VB binding) {
        this.binding = binding;
        onCreateReal();
    }

    public void wrapWithState(ViewStateConfig config){
        statefulLayout = StatefulLayout.wrapWithState(binding.getRoot(),config);
    }



    public View getRootView(){
        if(statefulLayout == null){
            return   binding.getRoot();
        }
        return statefulLayout;
    }

    protected void onCreateReal() {
         LifecycleOwner lifecycleOwner =  LifecycleObjectUtil.getLifecycleOwnerFromObj(binding.getRoot());
        if(lifecycleOwner != null){
            lifecycleOwner.getLifecycle().addObserver(this);
            //onCreate(lifecycleOwner);
        }
    }

    public MyViewHolder(Context context){
        Activity activityFromContext = LifecycleObjectUtil.getActivityFromContext(context);
        if(activityFromContext == null){
            throw  new RuntimeException("getActivityFromContext(context) is null");
        }
        initByViewGroup(activityFromContext.findViewById(android.R.id.content));
    }


    public MyViewHolder(ViewGroup parent) {
        initByViewGroup(parent);

    }

    private   void initByViewGroup(ViewGroup parent){
        try {
            Class bindingClass = GenericClassUtil.getGenericFromSuperClass(getClass(),0);
            if(bindingClass != null){
                binding =  ReflectUtils.reflect(bindingClass)
                        .method("inflate", LayoutInflater.from(parent.getContext()),parent,false)
                        .get();
            }
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            VB binding2 =   createBinding(parent);
            if(binding2 != null){
                binding = binding2;
            }
        }

        onCreateReal();
        if(binding.getRoot() instanceof StatefulLayout){
            statefulLayout = (StatefulLayout) binding.getRoot();
        }
    }


    protected  Dialog dialog;
    public Dialog showInFullScreenDialog(){
        FullScreenDialog fullScreenDialog = new FullScreenDialog(ActivityUtils.getTopActivity());
        getRootView().setPadding(0, BarUtils.getStatusBarHeight(),0,0);
        fullScreenDialog.setContentView(getRootView());

        this.dialog = fullScreenDialog;
        fullScreenDialog.show();
        BarUtils.setStatusBarLightMode(fullScreenDialog.getWindow(),true);
        return fullScreenDialog;
    }

    public Dialog showInDialog(){
       return null;
    }


    /**
     * 默认使用反射,反射出问题才调用这个方法
     * 模板代码:   binding = SubmitItemPriceInputBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
     * @param parent
     */
    @Deprecated
    protected  VB createBinding(ViewGroup parent){
        return null;
    }

    public  MyViewHolder<VB,T> assignDataAndEvent(T data){
        this.data = data;
        assignDataAndEventReal(data);
        return this;
    }


    public void onDestory() {
        onDestroy(LifecycleObjectUtil.getLifecycleOwnerFromObj(binding.getRoot().getContext()));
    }



    protected abstract void assignDataAndEventReal(T data);


    public   void assignDataAndEvent(@Nullable T bean, int position){
        assignDataAndEvent(bean);
    }

    public   void assignDataAndEvent(@Nullable T bean, int position, Object extra,
                                     boolean isLast, List datas, SuperViewGroupSingleAdapter2 adapter){
        assignDataAndEvent(bean,position);
    }
}
