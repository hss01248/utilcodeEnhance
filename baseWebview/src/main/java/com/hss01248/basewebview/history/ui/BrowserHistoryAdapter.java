package com.hss01248.basewebview.history.ui;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.history.db.BrowserHistoryInfo;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 20:04
 * @Version 1.0
 */
public class BrowserHistoryAdapter extends BaseQuickAdapter<BrowserHistoryInfo, BaseViewHolder> {
    public BrowserHistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setQuickWebview(BaseQuickWebview quickWebview) {
        this.quickWebview = quickWebview;
    }

    BaseQuickWebview quickWebview;
    @Override
    protected void convert(BaseViewHolder helper, BrowserHistoryInfo item) {
        helper.setText(R.id.tv_title,item.title);
        helper.setText(R.id.tv_url,item.url);

        helper.setOnClickListener(R.id.rl_root, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickWebview.loadUrl(item.url);
                if(HistoryCollectVpHolder.dialog != null){
                    HistoryCollectVpHolder.dialog.dismiss();
                }
            }
        });

    }
}
