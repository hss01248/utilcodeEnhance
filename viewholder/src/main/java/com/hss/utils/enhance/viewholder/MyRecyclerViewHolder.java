package com.hss.utils.enhance.viewholder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 3:12 PM
 * @Version 1.0
 */
public abstract class MyRecyclerViewHolder<VB extends ViewBinding,T> extends RecyclerView.ViewHolder implements DefaultLifecycleObserver {

    protected  VB binding;

    public MyRecyclerViewHolder<VB,T> setBinding(VB binding) {
        this.binding = binding;
        return this;
    }

    public MyRecyclerViewHolder(@NonNull View itemView) {
        //利用aop将这个NonNull干掉,变成可以为null. 或者aop直接切入这个方法的before,给itemview赋值
        super(itemView);
        doInit(itemView.getContext(), this);
    }




    private static  void doInit(Context context, LifecycleObserver object) {
        LifecycleObjectUtil.getLifecycleOwnerFromObj(context).getLifecycle().addObserver(object);

    }

    /**
     * 如果有需要，才实现这个方法
     * @param data 该条目的数据
     * @param position 该条目所在的位置
     * @param isLast 是否为最后一条,有些情况下需要用到
     * @param isListViewFling listview是不是在惯性滑动,备用
     *  @param datas 整个listview对应的数据
     * @param superRecyAdapter adapter对象引用,可用于触发notifydatesetChanged()方法刷新整个listview,比如更改的单选按钮
     */
    public  void assignDatasAndEvents( T data, int position,
                                      boolean isLast, boolean isListViewFling,
                                       List datas,
                                       MyRecyclerViewAdapter superRecyAdapter){
        assignDatasAndEvents(data);
    }

    /**
     * 一般情况下实现此方法
     * @param data
     */
    public  abstract   void assignDatasAndEvents( T data);


}
