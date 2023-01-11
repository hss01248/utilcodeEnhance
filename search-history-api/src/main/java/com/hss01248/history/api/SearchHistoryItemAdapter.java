package com.hss01248.history.api;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hss01248.history.api.db.SearchDbUtil;

/**
 * @Despciption todo
 * @Author hss
 * @Date 14/06/2022 11:50
 * @Version 1.0
 */
public class SearchHistoryItemAdapter extends BaseQuickAdapter<SearchHistoryItem, BaseViewHolder> {
    public SearchHistoryItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchHistoryItem item) {
        helper.setText(R.id.tv_history_name,item.getItem());
        helper.setOnClickListener(R.id.iv_delete_item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDbUtil.delete(item.item);
                getData().remove(item);
                notifyDataSetChanged();
            }
        });
    }
}
