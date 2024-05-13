package com.hss.utils.enhance.viewholder.viewpager;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;
@Deprecated
public interface IViewPagerInstantiateItem<T> {

    View initView(ViewGroup container, List<T> datas, int position);


}
