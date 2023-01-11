package com.hss01248.history.api;

import android.text.TextUtils;
import android.view.View;

import com.hss01248.history.api.db.SearchDbUtil;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/01/2023 11:54
 * @Version 1.0
 */
public class SearchHistoryImpl implements ISearchHistory{
    @Override
    public void addToHistory(String content) {
        if(!TextUtils.isEmpty(content)){
            SearchDbUtil.addOrUpdate(content);
        }

    }

    @Override
    public View showHistoryPannel() {


        return null;
    }
}
