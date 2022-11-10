package com.hss.utils.enhance.viewholder.multitype;

import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/06/2022 11:52
 * @Version 1.0
 */
public abstract class ItemViewHolder<VB extends ViewBinding,T extends IItemType> implements ItemViewProvider<VB,T>{

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

    /**
     *   binding = SubmitItemPriceInputBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
     * @param parent
     */
    public abstract void initView(ViewGroup parent);

    /*@Override
    public int getType() {
        if(data != null){
            return data.getType();
        }else {
            return -1;
        }
    }*/

    public  void assignDataAndEvent(T data, int position){
        this.data = data;
        this.position = position;
        assignDataAndEventReal(data,position);
    }

    protected abstract void assignDataAndEventReal(T data, int position);
}
