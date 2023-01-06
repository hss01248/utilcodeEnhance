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
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.hss.utils.enhance.UrlEncodeUtil;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.activityresult.TheActivityListener;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.ISetWebviewHolder;
import com.hss01248.basewebview.R;
import com.hss01248.basewebview.WebConfigger;
import com.hss01248.basewebview.WebDebugger;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;
import com.hss01248.toast.MyToast;
import com.just.agentweb.MiddlewareWebClientBase;

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
        BaseQuickWebview baseQuickWebview = new BaseQuickWebview(view.getContext(),null,new MiddlewareWebClientBase(){
            boolean isFirstOverride =true;
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view0, WebResourceRequest request) {
                if(!isFirstOverride){
                    return super.shouldOverrideUrlLoading(view0, request);
                }
                isFirstOverride = false;
                BaseQuickWebview quickWebview  = (BaseQuickWebview) view0.getParent().getParent().getParent();
                Uri uri = request.getUrl();
                if("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())){
                    //todo  判断是不是下载链接,如果是,直接调用下载,如果不是,才开启新的activity来承载
                    String path = uri.getPath();
                    //String name = URLUtil.guessFileName()
                    if(path.contains(".") && !path.endsWith(".")){
                        //String name = path.substring(path.lastIndexOf(".")+1);
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                        if(!TextUtils.isEmpty(mime)){
                            if(mime.startsWith("image") || mime.contains("text") || mime.contains("xml") || mime.contains("json")){

                            }else {
                                //这里触发下载:
                                view.loadUrl(uri.toString());
                                quickWebview.onDestroy((LifecycleOwner) WebDebugger.getActivityFromContext(view.getContext()));
                                return true;
                            }
                        }
                    }

                }else {
                    // 弹窗提示是否跳到app打开
                    //todo URLUtil.isAboutUrl()
                    new AlertDialogImplByDialogUtil().showMsg("跳转", "是否打开此链接?\n" + UrlEncodeUtil.decode(uri.toString()),
                            "打开", "取消", new BaseDialogListener() {
                                @Override
                                public void onConfirm() {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(uri);
                                        ActivityUtils.getTopActivity().startActivity(intent);
                                    }catch (Throwable throwable){
                                        LogUtils.w(throwable);
                                        MyToast.error("没有应用能打开这个链接: \n"+ UrlEncodeUtil.decode(uri.toString()));
                                    }

                                }
                            });
                    quickWebview.onDestroy((LifecycleOwner) WebDebugger.getActivityFromContext(view.getContext()));
                    return true;
                }

                //到了这里,才是用activity打开页面
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

                                    activity.setContentView(quickWebview);
                                    quickWebview.resetContext(activity);
                                    quickWebview.getChildAt(1).setLeft(0);

                                    ViewGroup.LayoutParams layoutParams = quickWebview.getChildAt(1).getLayoutParams();
                                    layoutParams.width = ScreenUtils.getScreenWidth();
                                    quickWebview.getChildAt(1).setLayoutParams(layoutParams);

                                    //BaseQuickWebview quickWebview = activity.findViewById(R.id.root_ll);
                                    holder.setWebviewHolder(quickWebview);



                                   /* LogUtils.w("debugwebview", "onCreateWindow:isDialog:" + isDialog +
                                                    ",isUserGesture:" + isUserGesture + ",msg:" + resultMsg + "\n chromeclient:" + this+","+newWebView,
                                            newWebView.getUrl(),newWebView.getOriginalUrl(),resultMsg.obj);*/

                                    //shouldOverrideUrlLoading()
                                    //给新打开的webview响应closewindow用
                                    //quickWebview.jsCreateNewWin = JsCreateNewWinImpl.this;
                                    quickWebview.jsCreateNewWin.activity = activity;
                                }
                            }
                        });


                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(baseQuickWebview.getWebView());

        resultMsg.sendToTarget();

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
