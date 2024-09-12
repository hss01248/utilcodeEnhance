package com.hss01248.basewebview.history.ui;

import android.view.ViewGroup;

import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.databinding.ContainerHistoryCollectBinding;
import com.hss01248.basewebview.history.db.BrowserHistoryInfo;
import com.hss01248.refresh_loadmore.search.SearchViewHolder;

import java.util.HashMap;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 17:39
 * @Version 1.0
 */
public class HistoryOrCollectViewHolder extends MyViewHolder<ContainerHistoryCollectBinding, BaseQuickWebview> {
    public HistoryOrCollectViewHolder(ViewGroup parent) {
        super(parent);
    }

    boolean isCollect;

    SearchViewHolder<BrowserHistoryInfo> holder;
    @Override
    protected void assignDataAndEventReal(BaseQuickWebview data) {
        holder = new SearchViewHolder<BrowserHistoryInfo>(binding.getRoot().getContext());
        binding.getRoot().addView(holder.binding.getRoot());
        //binding.getRoot().setPadding(0, BarUtils.getStatusBarHeight(),0,0);

        holder.getLoadMoreRecycleViewHolder().getDto().pageSize = 60;
        holder.getLoadMoreRecycleViewHolder().initRecyclerViewDefault();

        holder.getLoadMoreRecycleViewHolder().setEmptyMsg(isCollect? "收藏": "访问记录"+" 为空");
        holder.getLoadMoreRecycleViewHolder().setLoadDataImpl(new LoadDataByHistoryDb(isCollect));
        BrowserHistoryAdapter adapter = new BrowserHistoryAdapter(R.layout.item_history_collect);
        adapter.setQuickWebview(data);
        holder.getLoadMoreRecycleViewHolder().setAdapter(adapter);
        holder.getLoadMoreRecycleViewHolder().assignDataAndEvent(new HashMap<>());

        //holder.getHistoryViewHolder().assignDataAndEvent("");
    }
}
