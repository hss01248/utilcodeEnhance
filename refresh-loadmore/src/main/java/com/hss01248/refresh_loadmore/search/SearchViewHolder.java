package com.hss01248.refresh_loadmore.search;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.history.api.OnHistoryItemClickListener;
import com.hss01248.history.api.SearchHistoryViewHolder;
import com.hss01248.history.api.db.SearchDbUtil;
import com.hss01248.refresh_loadmore.RefreshLoadMoreRecycleViewHolder;
import com.hss01248.refresh_loadmore.databinding.CommonSearchViewHolderBinding;

import java.util.HashMap;

/**
 * @author: Administrator
 * @date: 2023/2/5
 * @desc: //todo
 */
public class SearchViewHolder<T> extends MyViewHolder<CommonSearchViewHolderBinding,String> {
    public SearchViewHolder(Context context) {
        super(context);
        initView();
        initEvent();
        initContentView();
    }

    public SearchHistoryViewHolder getHistoryViewHolder() {
        return historyViewHolder;
    }

    SearchHistoryViewHolder historyViewHolder;

    public RefreshLoadMoreRecycleViewHolder<T> getLoadMoreRecycleViewHolder() {
        return loadMoreRecycleViewHolder;
    }

    RefreshLoadMoreRecycleViewHolder<T> loadMoreRecycleViewHolder;
    @Override
    protected void assignDataAndEventReal(String data) {

    }

    private void initContentView() {
        loadMoreRecycleViewHolder = new RefreshLoadMoreRecycleViewHolder<T>(getRootView().getContext());
       // loadMoreRecycleViewHolder.setLoadDataImpl();
        binding.rlContainer.addView(loadMoreRecycleViewHolder.getRootView());
        loadMoreRecycleViewHolder.getStatefulLayout().showContent();
        //loadMoreRecycleViewHolder.getRootView()

    }

    private void initView() {
        historyViewHolder = new SearchHistoryViewHolder((ViewGroup) getRootView());
        historyViewHolder.assignDataAndEvent("");
        binding.rlContainer.addView(historyViewHolder.getRootView());
        historyViewHolder.getRootView().setVisibility(View.GONE);
    }

    private void initEvent() {
        binding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etInput.setText("");
            }
        });
        binding.etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyViewHolder.assignDataAndEvent("");
            }
        });
        historyViewHolder.setItemClickListener(new OnHistoryItemClickListener() {
            @Override
            public void onHistoryItemClick(String text) {
                binding.etInput.setText(text);
                //doSearch(text);
            }
        });

        binding.etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ) {
                    doSearch0();
                    //此处做逻辑处理
                    return true;
                }
                return false;
            }
        });


        binding.tvDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch0();
            }
        });
    }

    private void doSearch0() {
        String text = binding.etInput.getText().toString().trim();
        SearchDbUtil.addOrUpdate(text);
        historyViewHolder.getRootView().setVisibility(View.GONE);
        doSearch(text);
    }

    public String searchText(){
        return binding.etInput.getText().toString().trim();
    }
    private void doSearch(String text) {
        loadMoreRecycleViewHolder.loadByNewParams(text,new HashMap<>());
    }
}
