package com.hss01248.basewebview;

import android.content.Context;
import android.util.AttributeSet;

import com.just.agentweb.AgentWebView;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

/**
 * @Despciption  extends AgentWebView
 * @Author hss
 * @Date 12/01/2023 17:40
 * @Version 1.0
 */
public class WrappedWebview extends AdblockWebView {
    public WrappedWebview(Context context) {
        super(context);
    }

    public WrappedWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
