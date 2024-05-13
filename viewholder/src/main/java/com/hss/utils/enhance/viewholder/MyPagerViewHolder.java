package com.hss.utils.enhance.viewholder;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.DefaultLifecycleObserver;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 10:15 AM
 * @Version 1.0
 */
public abstract class MyPagerViewHolder<T> implements DefaultLifecycleObserver {


    public View rootView;
    public MyPagerViewHolder(Context context) {
    }


    public  abstract void assingDatasAndEvents(Context context, List datas, T data, int position, MyPagerAdapter dataAdapter);
}
