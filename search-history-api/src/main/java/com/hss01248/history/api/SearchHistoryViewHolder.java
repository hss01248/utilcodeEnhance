package com.hss01248.history.api;

import android.view.View;
import android.view.ViewGroup;


import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.history.api.databinding.MySearchHistoryBinding;
import com.hss01248.history.api.db.SearchDbUtil;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/01/2023 11:53
 * @Version 1.0
 */
public class SearchHistoryViewHolder extends MyViewHolder<MySearchHistoryBinding,String> {
    public SearchHistoryViewHolder(ViewGroup parent) {
        super(parent);
    }
    SearchHistoryItemAdapter historyItemAdapter;

    public void setItemClickListener(OnHistoryItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    OnHistoryItemClickListener itemClickListener;
    @Override
    protected void assignDataAndEventReal(String data) {
        List<SearchHistoryItem> historyItems = SearchDbUtil.loadAll();
        if(historyItems == null || historyItems.isEmpty()){
            binding.getRoot().setVisibility(View.GONE);
            return;

        }
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        if(layoutParams == null){
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        layoutParams.width = ScreenUtils.getScreenWidth();
        binding.getRoot().setLayoutParams(layoutParams);

        binding.getRoot().setVisibility(View.VISIBLE);
        //加载历史
        if(historyItemAdapter == null){
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getRootView().getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            //layoutManager.setAlignItems(AlignItems.);
            binding.recyclerView.setLayoutManager(layoutManager);
            // binding.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            // binding.recyclerView.addItemDecoration(ItemListPagerAdapter.getHorizontalDividerItemDecoration(this));
            historyItemAdapter = new SearchHistoryItemAdapter(R.layout.my_search_history_item);
            binding.recyclerView.setAdapter(historyItemAdapter);
            historyItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    String str = historyItems.get(position).getItem();
                    itemClickListener.onHistoryItemClick(str);
                    SearchDbUtil.addOrUpdate(str);
                }
            });
        }
        historyItemAdapter.replaceData(historyItems);

        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialogImplByDialogUtil().showMsg("tips", "是否清空所有搜索记录", "清空", "取消",
                        new BaseDialogListener() {
                            @Override
                            public void onConfirm() {
                                BaseDialogListener.super.onConfirm();
                                SearchDbUtil.clear();
                                binding.getRoot().setVisibility(View.GONE);
                            }
                        });
            }
        });
        
    }

    public void refreshData(){
        List<SearchHistoryItem> historyItems = SearchDbUtil.loadAll();
        if(historyItems == null || historyItems.isEmpty()){
            binding.getRoot().setVisibility(View.GONE);
            historyItemAdapter.replaceData(new ArrayList<>());
            return;

        }
        binding.getRoot().setVisibility(View.VISIBLE);
        historyItemAdapter.replaceData(historyItems);
    }
}
