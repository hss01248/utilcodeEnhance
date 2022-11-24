package com.hss.utils.enhance.viewholder.multitype;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Despciption 页面模块化利器
 * 普通ViewGroup的多类型adapter,非recycledview类型,adapterview类型
 * @Author hss
 * @Date 21/06/2022 11:45
 * @Version 1.0
 */
public class NormalViewGroupMultiTypeAdapter {


    Map<Integer,ItemViewProvider> providerMap = new HashMap<>();

    public List<ItemViewHolder> getViewHolders() {
        return viewHolders;
    }

    List<ItemViewHolder> viewHolders = new ArrayList<>();

    public void addItemViewProvider(ItemViewProvider provider){
        providerMap.put(provider.getType(),provider);
    }

    public void setData(ViewGroup viewGroup, List<? extends IItemType> datas){
        //todo 性能优化: 只更新数据,不重建view
        viewGroup.removeAllViews();
        viewHolders.clear();;
        for (int i = 0; i < datas.size(); i++) {
            IItemType data = datas.get(i);
            if(!providerMap.containsKey(data.getType())){
                LogUtils.e("type not fit the ItemViewProvider "+data.getType());
                continue;
            }
            ItemViewHolder viewHolder = providerMap.get(data.getType()).createViewHolder();
            viewHolder.initView(viewGroup);
            viewGroup.addView(viewHolder.binding.getRoot());
            viewHolders.add(viewHolder);
            viewHolder.assignDataAndEvent(data,i);
        }
    }




}
