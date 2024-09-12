package com.hss01248.viewholder_media;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.fondesa.recyclerviewdivider.DividerDecoration;
import com.hss.utils.enhance.viewholder.MyRecyclerViewAdapter;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss01248.viewholder_media.databinding.LayoutFileItemGridBinding;
import com.hss01248.viewholder_media.databinding.LayoutFileItemLinearBinding;
import com.hss01248.viewholder_media.databinding.LayoutMediaListBinding;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/14/24 9:29 AM
 * @Version 1.0
 */
public class MediaListViewHolder extends BaseViewHolder<LayoutMediaListBinding, List<String>> {
    public MediaListViewHolder(Context context) {
        super(context);
    }
    public MediaListViewHolder setOnItemClicked(Consumer<String> onItemClicked) {
        this.onItemClicked = onItemClicked;
        return this;
    }

    Consumer<String> onItemClicked;
    MyRecyclerViewAdapter adapter;
    RecyclerView.ItemDecoration dividerDecoration;

    public void setFilterInfo(DisplayAndFilterInfo filterInfo) {
        /*if(filterInfo.displayType != this.filterInfo.displayType){
            adapter = null;
            dividerDecoration =null;
        }*/
        adapter = null;
        dividerDecoration =null;
        this.filterInfo = filterInfo;
        doUpdate();
    }

    DisplayAndFilterInfo filterInfo = new DisplayAndFilterInfo();

    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, List<String> bean) {

        if(filterInfo.displayType == 0){
            if(gridLayoutManager ==null){
                gridLayoutManager = new GridLayoutManager(context,FileItemGridViewHolder.getSpanCount());
            }
            binding.recyclerView.setLayoutManager(gridLayoutManager);
        }else {
            if(linearLayoutManager ==null){
                linearLayoutManager = new LinearLayoutManager(context);
            }
            binding.recyclerView.setLayoutManager(linearLayoutManager);
        }
        if(adapter ==null){
            adapter = new MyRecyclerViewAdapter() {
                @Override
                protected MyRecyclerViewHolder generateNewViewHolder(int viewType) {

                    if(filterInfo.displayType == 0){
                        LayoutFileItemGridBinding inflate = LayoutFileItemGridBinding.inflate(
                                LayoutInflater.from(context), binding.getRoot(), false);
                        return new FileItemGridViewHolder(inflate.getRoot())
                                .setOnItemClicked(onItemClicked)
                                .setFilterInfo(filterInfo)
                                .setBinding(inflate);
                    }else {
                        LayoutFileItemLinearBinding inflate = LayoutFileItemLinearBinding.inflate(
                                LayoutInflater.from(context), binding.getRoot(), false);
                        return new FileItemLinearViewHolder(inflate.getRoot())
                                .setOnItemClicked(onItemClicked)
                                .setBinding(inflate);
                    }
                }
            };
            binding.recyclerView.setAdapter(adapter);
        }
        if(dividerDecoration ==null){
            dividerDecoration = (RecyclerView.ItemDecoration)(DividerDecoration.builder(context)
                    .color(Color.parseColor(filterInfo.displayType ==0 ? "#ffffff" : "#f0f2f5"))
                    .size(SizeUtils.dp2px(FileItemGridViewHolder.dividerWidth)).build());
            binding.recyclerView.addItemDecoration(dividerDecoration);
        }
        adapter.refresh(bean);
    }
}
