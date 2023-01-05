package com.hss01248.basewebview.dom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.activityresult.TheActivityListener;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.ISetWebviewHolder;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.WebConfigger;

public class JsCreateNewWinImpl {



     Activity activity;

     public static void enableMultipulWindow(WebView webView, boolean supportMultiplWindow){
         WebSettings mWebSettings = webView.getSettings();
         mWebSettings.setSupportMultipleWindows(supportMultiplWindow);
         mWebSettings.setJavaScriptCanOpenWindowsAutomatically(supportMultiplWindow);//支持通过js打开新的窗口
     }

    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
         //下载,以及其他协议的链接,就不要打开新webview
        LogUtils.w(view.getUrl(),view.getOriginalUrl());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onCreateWindow2( view,  isDialog,  isUserGesture,  resultMsg);
            }
        });
        return true;
    }

    private void onCreateWindow2(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

         //todo 先打开
        BaseQuickWebview baseQuickWebview = new BaseQuickWebview(view.getContext());
        WebView  newWebView = baseQuickWebview.getWebView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WebViewClient webViewClient = newWebView.getWebViewClient();
            newWebView.setWebViewClient(new WebViewClient(){
                //todo  其他的使用原webviewclient

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Uri uri = request.getUrl();
                    if("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())){
                        //todo  判断是不是下载链接,如果是,直接调用下载,如果不是,才开启新的activity来承载

                    }else {
                        //todo 弹窗提示是否跳到app打开
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });

        }


        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);

        resultMsg.sendToTarget();



        Intent intent = new Intent(ActivityUtils.getTopActivity(), WebConfigger.getInit().html5ActivityClass());
        intent.putExtra(ISetWebviewHolder.setWebviewHolderByOutSide,true);
        StartActivityUtil.startActivity(ActivityUtils.getTopActivity(),
                WebConfigger.getInit().html5ActivityClass(),intent,
                false, new TheActivityListener<AppCompatActivity>(){

                    @Override
                    protected void onActivityCreated(@NonNull AppCompatActivity activity, @Nullable Bundle savedInstanceState) {
                        super.onActivityCreated(activity, savedInstanceState);
                        if(activity instanceof ISetWebviewHolder){
                        /*    BaseQuickWebview quickWebview = new BaseQuickWebview(activity);
                            ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                            quickWebview.setLayoutParams(layoutParams);*/
                            ISetWebviewHolder holder = (ISetWebviewHolder) activity;

                            activity.setContentView(R.layout.default_webview_container);
                            BaseQuickWebview quickWebview = activity.findViewById(R.id.root_ll);
                            holder.setWebviewHolder(quickWebview);

                            //相当于load url
                           WebView  newWebView = quickWebview.getWebView();

                            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                            transport.setWebView(newWebView);
                            resultMsg.sendToTarget();

                            LogUtils.w("debugwebview", "onCreateWindow:isDialog:" + isDialog +
                                    ",isUserGesture:" + isUserGesture + ",msg:" + resultMsg + "\n chromeclient:" + this+","+newWebView,
                                    newWebView.getUrl(),newWebView.getOriginalUrl(),resultMsg.obj);

                            //shouldOverrideUrlLoading()
                            //给新打开的webview响应closewindow用
                            //quickWebview.jsCreateNewWin = JsCreateNewWinImpl.this;
                            quickWebview.jsCreateNewWin.activity = activity;
                        }
                    }
                });
    }


    /**
     * 子窗口收到父窗口的window.close()方法,或者自己的window.close()方法
     * @param window
     */
    public void onCloseWindow(WebView window) {
        if(activity != null){
            activity.finish();
        }

    }
}
