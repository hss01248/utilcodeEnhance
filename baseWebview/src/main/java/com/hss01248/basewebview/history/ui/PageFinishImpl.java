package com.hss01248.basewebview.history.ui;

import android.webkit.URLUtil;
import android.webkit.WebView;

import com.hss01248.basewebview.history.db.MyDbUtil;
import com.just.agentweb.MiddlewareWebClientBase;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 20:33
 * @Version 1.0
 */
public class PageFinishImpl extends MiddlewareWebClientBase {

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(URLUtil.isNetworkUrl(url)){
            //MyDbUtil.addHistory();
        }
    }
}
