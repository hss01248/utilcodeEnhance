package com.hss.utils.enhance.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hss01248.viewholder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 10:07 AM
 * @Version 1.0
 */
public abstract class MyPagerAdapter extends PagerAdapter {

    List datas = new ArrayList();
    List<MyPagerViewHolder> cacheList = new ArrayList<>();
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
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MyPagerViewHolder viewHolder = null;
        if(cacheList.isEmpty()){
            viewHolder = generateNewHolder(container,position);
            viewHolder.rootView.setTag(R.id.tv_my_view_pager,viewHolder);
        }else {
            viewHolder =  cacheList.remove(0);
        }
        viewHolder.assingDatasAndEvents(container.getContext(), datas,datas.get(position),position,this);
        container.addView(viewHolder.rootView);
        return viewHolder;
    }

    protected abstract MyPagerViewHolder generateNewHolder( ViewGroup container, int position);

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
        cacheList.add((MyPagerViewHolder) view.getTag(R.id.tv_my_view_pager));
    }
}
