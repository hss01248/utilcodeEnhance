package com.hss.utils.enhance.viewholder.multitype;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/06/2022 11:52
 * @Version 1.0
 */
public abstract class ItemViewHolder<VB extends ViewBinding,T extends IItemType>
        implements ItemViewProvider<VB,T> , DefaultLifecycleObserver {

    public int getPosition() {
        return position;
    }

    public VB getBinding() {
        return binding;
    }

    public T getData() {
        return data;
    }

    protected int position;
    protected VB binding;
    protected T data;

    public ItemViewHolder() {

    }


      void initView(@NonNull ViewGroup parent){
        LifecycleOwner lifecycleOwner =  LifecycleObjectUtil.getLifecycleOwnerFromObj(parent);
        if(lifecycleOwner != null){
            lifecycleOwner.getLifecycle().addObserver(this);
            //onCreate(lifecycleOwner);
        }
        initViewReal(parent);
    }
    /**
     *   binding = SubmitItemPriceInputBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
     * @param parent
     */
    protected abstract void initViewReal(ViewGroup parent);


    public  void assignDataAndEvent(T data, int position){
        this.data = data;
        this.position = position;
        assignDataAndEventReal(data,position);
    }

    protected abstract void assignDataAndEventReal(T data, int position);
}
