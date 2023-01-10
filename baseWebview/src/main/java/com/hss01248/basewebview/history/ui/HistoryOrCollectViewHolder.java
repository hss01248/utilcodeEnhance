package com.hss01248.basewebview.history.ui;

import android.view.ViewGroup;

import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.databinding.ContainerHistoryCollectBinding;
import com.hss01248.basewebview.history.db.BrowserHistoryInfo;
import com.hss01248.refresh_loadmore.RefreshLoadMoreRecycleViewHolder;

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

    RefreshLoadMoreRecycleViewHolder<BrowserHistoryInfo> holder;
    @Override
    protected void assignDataAndEventReal(BaseQuickWebview data) {
        holder = new RefreshLoadMoreRecycleViewHolder<>(binding.getRoot());
        binding.getRoot().addView(holder.binding.getRoot());

        holder.setEmptyMsg("xxx 为空");
        holder.setLoadDataImpl(new LoadDataByHistoryDb(isCollect));
        holder.setAdapter(new BrowserHistoryAdapter(R.layout.item_history_collect));
        holder.assignDataAndEvent(new HashMap());
    }
}
