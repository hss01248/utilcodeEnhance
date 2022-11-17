package com.hss.utils.enhance.viewholder;



import android.view.ViewGroup;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;

/**
 * @Despciption ViewBinding可以外面传入(include标签部分),也可以内部自己生成
 * @Author hss
 * @Date 22/06/2022 16:27
 * @Version 1.0
 */
public abstract  class MyViewHolder<VB extends ViewBinding,T> implements DefaultLifecycleObserver {

    public VB binding;
    public T data;

    public MyViewHolder(VB binding) {
        this.binding = binding;
        onCreateReal();
    }

    protected void onCreateReal() {
         LifecycleOwner lifecycleOwner =  LifecycleObjectUtil.getLifecycleOwnerFromObj(binding.getRoot());
        if(lifecycleOwner != null){
            lifecycleOwner.getLifecycle().addObserver(this);
            //onCreate(lifecycleOwner);
        }

    }

    public MyViewHolder(ViewGroup parent) {
        binding =   createBinding(parent);
        onCreateReal();
    }
    /**
     * 模板代码:   binding = SubmitItemPriceInputBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
     * @param parent
     */
    protected  VB createBinding(ViewGroup parent){
        return null;
    }

    public  void assignDataAndEvent(T data){
        this.data = data;
        assignDataAndEventReal(data);
    }

    protected abstract void assignDataAndEventReal(T data);
}
