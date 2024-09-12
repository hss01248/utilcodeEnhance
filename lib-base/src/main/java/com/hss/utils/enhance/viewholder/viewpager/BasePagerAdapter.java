package com.hss.utils.enhance.viewholder.viewpager;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @date: 2022/2/26
 * @desc: //todo
 */
@Deprecated
public class BasePagerAdapter<T> extends PagerAdapter {
    public BasePagerAdapter(List<T> datas, IViewPagerInstantiateItem<T> instantiateItem) {
        if(datas != null){
            this.datas.addAll(datas);
        }
        this.instantiateItem = instantiateItem;
    }

    public List<T> getDatas() {
        return datas;
    }

    List<T> datas = new ArrayList<>();

    IViewPagerInstantiateItem<T> instantiateItem;

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull View container, int position) {
        View view = instantiateItem.initView((ViewGroup) container,datas,position);
        ((ViewGroup)container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        ((ViewGroup)container).removeView((View) object);
    }
}
