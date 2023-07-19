package com.hss01248.basewebview.adblock;

import android.content.Context;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.just.agentweb.MiddlewareWebClientBase;

import java.util.HashMap;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 12/01/2023 09:44
 * @Version 1.0
 */
public class AdBlockClient extends MiddlewareWebClientBase {


    public AdBlockClient(Context context) {
        AdBlocker.init(context.getApplicationContext());
    }

   static Map<String,Boolean> loadedUrls = new HashMap<>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        String url = request.getUrl().toString().toLowerCase();

        return isAd(url) ? AdBlocker.createEmptyResource() :
                super.shouldInterceptRequest(view, url);
    }

    public static boolean isAd(String url){
        boolean ad;
        if (!loadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            loadedUrls.put(url, ad);
        } else {
            ad = loadedUrls.get(url);
        }
        return ad;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString().toLowerCase();
        if(isAd(url)){
            return true;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }
}
