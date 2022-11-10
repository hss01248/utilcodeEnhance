package com.hss.utils.enhance.viewholder;

import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

/**
 * @Despciption todo
 * @Author hss
 * @Date 22/06/2022 16:27
 * @Version 1.0
 */
public abstract  class MyViewHolder<VB extends ViewBinding,T> {

    public VB binding;
    public T data;
    public MyViewHolder(VB binding) {
        this.binding = binding;
    }

    public MyViewHolder(ViewGroup parent) {
        binding =   createBinding(parent);
    }
    /**
     *   binding = SubmitItemPriceInputBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
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
