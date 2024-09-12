package com.hss01248.basewebview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.MyKeyboardUtil;
import com.hss.utils.enhance.UrlEncodeUtil;
import com.hss01248.basewebview.adblock.AdBlockClient;
import com.hss01248.basewebview.databinding.TitlebarForWebviewBinding;
import com.hss01248.basewebview.dom.AuthImpl;
import com.hss01248.basewebview.dom.FileChooseImpl;
import com.hss01248.basewebview.dom.JsCreateNewWinImpl;
import com.hss01248.basewebview.dom.JsPermissionImpl;
import com.hss01248.basewebview.download.WebviewDownladListenerImpl;
import com.hss01248.basewebview.history.db.MyDbUtil;
import com.hss01248.basewebview.menus.DefaultMenus;
import com.hss01248.basewebview.search.WebSearchViewHolder;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;
import com.hss01248.viewstate.StatefulLayout;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebUIControllerImplBase;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;


public class BaseQuickWebview extends LinearLayout implements DefaultLifecycleObserver {


    String currentUrl = "";

    public void setTitleFromIntent(String titleFromIntent) {
        this.titleFromIntent = titleFromIntent;
    }

    String titleFromIntent  = "";
    public AgentWeb getAgentWeb() {
        return mAgentWeb;
    }

    AgentWeb mAgentWeb;
    long delayAfterOnFinish = 500;
    AgentWeb.PreAgentWeb preAgentWeb;

    public void addRightMenus(IShowRightMenus showRightMenus) {
        List<ISingleChooseItem<BaseQuickWebview>> ms = showRightMenus.addMenus(this);
        if (ms != null) {
            menus.addAll(ms);
        }
    }


    public WebView getWebView() {
        return webView;
    }

    WebView webView;

    public String getCurrentTitle() {
        return currentTitle;
    }

    String currentTitle;

    public String getCurrentUrl() {
        return currentUrl;
    }

    WebDebugger debugger;
    String source;

    public WebPageInfo getInfo() {
        return info;
    }

    WebPageInfo info;
    MiddlewareWebChromeBase middlewareWebChrome;
    MiddlewareWebClientBase middlewareWebClient;
    List<ISingleChooseItem<BaseQuickWebview>> menus = new ArrayList<>();


    public BaseQuickWebview(Context context, MiddlewareWebChromeBase middlewareWebChrome, MiddlewareWebClientBase middlewareWebClient) {
        super(context);
        this.middlewareWebChrome = middlewareWebChrome;
        this.middlewareWebClient = middlewareWebClient;
        init(context);
    }

    public BaseQuickWebview(Context context) {
        super(context);
        init(context);
    }

    public BaseQuickWebview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseQuickWebview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseQuickWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        info = new WebPageInfo();
        initTitlebar(context);

        Activity activity = WebDebugger.getActivityFromContext(context);
        if (activity instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) activity;
            addLifecycle(owner);
        }
        initWebView();
        menus.addAll(new DefaultMenus().addMenus(this));

        webView.setOnLongClickListener(new TheLongPressListener(this));
        MyKeyboardUtil.adaptView(this);
    }

    public void resetContext(Context context) {
        AppCompatActivity activity0 = (AppCompatActivity) WebDebugger.getActivityFromContext(getContext());
        AppCompatActivity activity = (AppCompatActivity) WebDebugger.getActivityFromContext(context);
        if (activity == activity0) {
            return;
        }
        activity0.getLifecycle().removeObserver(this);
        activity.getLifecycle().addObserver(this);
        BarUtils.setStatusBarColor(activity, Color.WHITE);
        BarUtils.setStatusBarLightMode(activity, true);
    }

    public void resetLifecycleOwner(LifecycleOwner lifecycleOwner) {
        AppCompatActivity activity0 = (AppCompatActivity) WebDebugger.getActivityFromContext(getContext());
        if (activity0 == lifecycleOwner) {
            return;
        }
        activity0.getLifecycle().removeObserver(this);
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public TitlebarForWebviewBinding getTitleBar() {
        return titleBar;
    }

    TitlebarForWebviewBinding titleBar;

    public WebViewTitlebarHolder getTitlebarHolder() {
        return titlebarHolder;
    }

    WebViewTitlebarHolder titlebarHolder;

    private void initTitlebar(Context context) {

        Activity activity = WebDebugger.getActivityFromContext(context);
        BarUtils.setStatusBarColor(activity, Color.WHITE);
        BarUtils.setStatusBarLightMode(activity, true);

        titlebarHolder = new WebViewTitlebarHolder(this);
        titleBar = titlebarHolder.binding;
        titleBar.getRoot().setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);

        addView(titleBar.getRoot());

        titlebarHolder.assignDataAndEventReal(this);

    }

    protected void showMenu() {
        ISingleChooseItem.showAsMenu(titleBar.ivMenu, menus, this);
    }

    public void getSource(ValueCallback<String> valueCallback) {
        if (!TextUtils.isEmpty(source)) {
            valueCallback.onReceiveValue(source);
            return;
        }
        loadSource(valueCallback);
    }

    public static BaseQuickWebview loadHtml(Context context, String url, long delayAfterOnFinish, ValueCallback<WebPageInfo> sourceLoadListener) {
        BaseQuickWebview quickWebview = new BaseQuickWebview(context);
        quickWebview.needBlockImageLoad = true;
        quickWebview.delayAfterOnFinish = delayAfterOnFinish;

/*
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTextView(quickWebview);
        dialog.show();
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.height = ScreenUtils.getAppScreenHeight()/2;
        attributes.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(attributes);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });*/


        quickWebview.setSourceLoadListener(new ValueCallback<WebPageInfo>() {
            @Override
            public void onReceiveValue(WebPageInfo value) {
                sourceLoadListener.onReceiveValue(value);
                if (quickWebview.getAgentWeb() != null) {
                    quickWebview.getAgentWeb().destroy();
                }
            }
        });
        quickWebview.loadUrl(url);
        return quickWebview;

    }

    ValueCallback<WebPageInfo> sourceLoadListener;


    public void setNeedBlockImageLoad(boolean needBlockImageLoad) {
        this.needBlockImageLoad = needBlockImageLoad;
    }

    boolean needBlockImageLoad;

    public void setSourceLoadListener(ValueCallback<WebPageInfo> sourceLoadListener) {
        this.sourceLoadListener = sourceLoadListener;
    }


    public void loadSource(ValueCallback<String> valueCallback) {
        if (webView == null) {
            Log.w("loadSource", "webview is null");
            return;
        }
//        if(TextUtils.isEmpty(source)){
//            valueCallback.onReceiveValue(source);
//            return;
//        }
        //String script = "javascript:document.getElementsByTagName('html')[0].innerHTML";
        String script = "javascript:document.getElementsByTagName('body')[0].innerHTML";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //在主线程执行,耗时好几s
            webView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    source = StringEscapeUtils.unescapeJava(value);
                    if (source.startsWith("\"")) {
                        source = source.substring(1);
                    }
                    if (source.endsWith("\"")) {
                        source = source.substring(0, source.length() - 1);
                    }
                    //source = "<html>"+source +"</html>";
                    source = "<body>" + source + "</body>";
                    LogUtils.v(source);
                    info.htmlSource = source;
                    valueCallback.onReceiveValue(source);
                }
            });
        }
    }

    boolean hasAdd;

    private void addLifecycle(LifecycleOwner lifecycleOwner) {
        if (hasAdd) {
            return;
        }
        lifecycleOwner.getLifecycle().addObserver(this);
        hasAdd = true;
    }

    public void loadUrl(String url) {
        if (url.startsWith("http")) {
            go(url);
        } else {
            if(url.contains("https://")){
                url = url.substring(url.indexOf("https://"));
                go(url);
                return;
            }else if(url.contains("http://")){
                url = url.substring(url.indexOf("http://"));
                go(url);
                return;
            }
            //调用百度/谷歌搜索
            int anInt = SPStaticUtils.getInt(WebSearchViewHolder.KEY_ENGIN,0);
            if(anInt <0 || anInt >2){
                anInt = 0;
            }
            String[] arr = {
                    "https://www.baidu.com/s?wd=" + url,
                    "https://www.google.com/search?q=" + url,
                    "https://www.bing.com/search?q=" + url

            };
            String url2 = arr[anInt];
            go(url2);
        }
    }


    StatefulLayout stateManager;

    public JsCreateNewWinImpl jsCreateNewWin = new JsCreateNewWinImpl();

    private void initWebView() {
        //OkhttpProxyForWebviewClient okhttpProxyForWebviewClient = new OkhttpProxyForWebviewClient();
        AgentWeb.CommonBuilder builder = AgentWeb.with((Activity) getContext())//传入Activity or Fragment
                .setAgentWebParent(this,
                        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                //.setWebView(new WrappedWebview(getContext()))
                .setAgentWebUIController(new AgentWebUIControllerImplBase() {
                    @Override
                    public void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {
                        //super.onMainFrameError(view, errorCode, description, failingUrl);
                        if (stateManager != null) {
                            stateManager.showError(errorCode + "\n" + description + "\n on url:" + failingUrl);
                        }
                    }

                    @Override
                    public void onOpenPagePrompt(WebView view, String url, Handler.Callback callback) {
                        super.onOpenPagePrompt(view, url, callback);

                        //ResolveInfo{e935c3a com.android.browser/.BrowserActivity m=0x208000}

                    }

                    @Override
                    public void onShowMainFrame() {
                        //super.onShowMainFrame();
                        if (stateManager != null) {
                            stateManager.showContent();
                        }
                    }

                    @Override
                    public void onJsAlert(WebView view, String url, String message) {
                        //super.onJsAlert(view, url, message);
                        new AlertDialogImplByDialogUtil().showMsg("tips", "来自" + UrlEncodeUtil.decode(view.getUrl()) + "的信息:\n" + message, "好的", "",
                                new BaseDialogListener() {
                                    @Override
                                    public void onConfirm() {
                                        BaseDialogListener.super.onConfirm();
                                    }
                                });
                    }
                })
                .setWebViewClient(new WebViewClient() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        //return new OkhttpProxyForWebview().shouldInterceptRequest(view,request);
                        return super.shouldInterceptRequest(view, request);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        String scheme = request.getUrl().getScheme();
                        String url = request.toString();
                        if (!TextUtils.isEmpty(scheme) &&
                                (scheme.startsWith("http") || scheme.startsWith("about") || scheme.startsWith("javascript"))) {
                            return super.shouldOverrideUrlLoading(view, request);
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(request.getUrl());
                        List<ResolveInfo> resolveInfos = Utils.getApp().getPackageManager().queryIntentActivities(intent, 0);
                        StringBuilder sb = new StringBuilder("(");
                        if (resolveInfos != null && !resolveInfos.isEmpty()) {
                            for (ResolveInfo resolveInfo : resolveInfos) {
                                //LogUtils.i(resolveInfo.resolvePackageName);
                                LogUtils.i(resolveInfo.toString());
                                // LogUtils.i(resolveInfo.labelRes);
                                String packageName = resolveInfo.activityInfo.packageName;
                                String appName = AppUtils.getAppName(packageName);
                                if (TextUtils.isEmpty(appName)) {
                                    appName = packageName;
                                }
                                sb.append(appName).append(",");
                            }
                        }
                        String str = sb.toString();
                        if (str.endsWith(",")) {
                            str = str.substring(0, str.length() - 1);
                        }
                        str = str + ")";

                        Dialog mAskOpenOtherAppDialog = new AlertDialog
                                .Builder(ActivityUtils.getTopActivity())
                                .setMessage(StringUtils.getString(R.string.agentweb_leave_app_and_go_other_page,
                                        AgentWebUtils.getApplicationName(ActivityUtils.getTopActivity())) + "\n" + str)
                                .setTitle(StringUtils.getString(R.string.agentweb_tips))
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                })//
                                .setPositiveButton(StringUtils.getString(R.string.agentweb_leave), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        ActivityUtils.getTopActivity().startActivity(intent);
                                    }
                                })
                                .create();

                        mAskOpenOtherAppDialog.show();
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        source = "";
                        currentUrl = url;
                        currentTitle = "";
                        info.htmlSource = "";
                        info.url = url;
                        info.title = "";
                        if (needBlockImageLoad) {
                            view.getSettings().setBlockNetworkImage(needBlockImageLoad);
                        }

                    }

                    /**
                     * onReceivedHttpError+main frame后,也会走到onPageFinished
                     * @param view
                     * @param url
                     */
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

//注入js:
                        /*String js = "var newscript = document.createElement(\"script\");";
                        js += "newscript.src=\"https://cdnjs.cloudflare.com/ajax/libs/eruda/3.0.1/eruda.min.js\";";
                        js += "document.body.appendChild(newscript);";

                        js += "var newscript2 = document.createElement(\"script\");";
                        js += "newscript2.text=\"eruda.init();\";";
                        js += "document.body.appendChild(newscript2);";
                        view.loadUrl("javascript:"+js);*/

                        //checkIfTouTiao(view,url);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadSource(new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        if (sourceLoadListener != null) {
                                            sourceLoadListener.onReceiveValue(info);
                                        }
                                        checkIfTouTiao2(value, url);
                                    }
                                });
                            }
                        }, delayAfterOnFinish);
                        if ("about:blank".equals(url)) {
                            if (stateManager != null) {
                                stateManager.showError("not url : \n" + originalUrl);
                            }
                        }
                        if (URLUtil.isNetworkUrl(url)) {
                            MyDbUtil.addHistory(getCurrentTitle(), url, "");
                        }

                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        if (stateManager != null) {
                            stateManager.showError(errorCode + "\n" + description + "\n on url:" + failingUrl);
                        }

                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        super.onReceivedHttpError(view, request, errorResponse);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (request.isForMainFrame()) {
                                if (stateManager != null) {
                                    stateManager.showError(errorResponse.getStatusCode() + "\n" + errorResponse.getReasonPhrase() + "\n on url:" + request.getUrl());
                                }

                            }
                        }
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                        new AlertDialogImplByDialogUtil().showMsg("https证书有误",
                                "http证书错误,是否继续请求?\n(证书错误表示网站可能不安全)\n证书信息如下:\n" + error.toString(),
                                "忽略错误,继续请求", "终止请求", new BaseDialogListener() {
                                    @Override
                                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                                        handler.cancel();
                                        if (error.getUrl().equals(view.getUrl())) {
                                            if (stateManager != null) {
                                                stateManager.showError("SslError:\n" + error.toString());
                                            }
                                        }

                                    }

                                    @Override
                                    public void onConfirm() {
                                        handler.proceed();
                                    }
                                });
                    }
                })
                .setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        //titleBar.tvTitle.setText(title);
                        titleBar.tvTitle.setText(title);
                        titleBar.tvTitle.requestFocus();
                        currentTitle = title;
                        info.title = title;
                    }

                    @Override
                    public void onReceivedIcon(WebView view, Bitmap icon) {
                        super.onReceivedIcon(view, icon);
                        /*if(titlebarHolder.isFullWebBrowserMode){
                            titleBar.ivBack.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            titleBar.ivBack.setImageBitmap(icon);
                        }*/

                    }

                    @Override
                    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                        return jsCreateNewWin.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                    }

                    @Override
                    public void onCloseWindow(WebView window) {
                        jsCreateNewWin.onCloseWindow(window);
                    }
                })
                .useMiddlewareWebClient(new AuthImpl())
                .useMiddlewareWebChrome(new JsPermissionImpl())
                .useMiddlewareWebClient(new AdBlockClient(getContext()))
                .useMiddlewareWebChrome(new FileChooseImpl());
        if (middlewareWebClient != null) {
            builder.useMiddlewareWebClient(middlewareWebClient);
        }
        if (middlewareWebChrome != null) {
            builder.useMiddlewareWebChrome(middlewareWebChrome);
        }
        //.useMiddlewareWebClient(okhttpProxyForWebviewClient)
        //.useMiddlewareWebChrome(new JsNewWindowImpl())
        //.useMiddlewareWebChrome(new VideoFullScreenImpl())
        // .setMainFrameErrorView(R.layout.pager_error,R.id.error_btn_retry)
        //.setMainFrameErrorView(errorLayout)
        preAgentWeb = builder.createAgentWeb()//
                .ready();

        mAgentWeb = preAgentWeb.get();

        webView = mAgentWeb.getWebCreator().getWebView();
        //okhttpProxyForWebviewClient.addAjaxInterceptorJsInterface(webView);
        stateManager = StatefulLayout.wrapWithState(mAgentWeb.getWebCreator().getWebParentLayout(), false, new Runnable() {
            @Override
            public void run() {
                stateManager.showContent();
                webView.reload();
            }
        });
        stateManager.showContent();
        WebConfigger.config(webView);
        debugger = new WebDebugger();
        debugger.setWebviewDebug(webView);
    }

    AlertDialog dialog;

    private void checkIfTouTiao2(String value, String url) {
        if (!url.startsWith("https://m.toutiao.com/")
                && !url.startsWith("https://www.iesdouyin.com/")
                && !url.startsWith("https://v.douyin.com/")) {
            return;
        }
        try {
            Document doc = Jsoup.parse(value);
            String url1 = "";
            if(url.startsWith("https://m.toutiao.com/")){
                Element video = doc.selectFirst("video");
                url1 = video.childNode(1).attr("src");
            }else if(url.startsWith("https://www.iesdouyin.com/")){
                Element video = doc.selectFirst("video");
                /*if(video == null){
                    return;
                }*/
                url1 = video.attr("src");
                if(!url1.startsWith("http")){
                    //url1 = "https://"+Uri.parse(url1).getHost()+url1;
                    url1 = "https://www.iesdouyin.com/"+url1;
                    //需要301重定向
                }
            }
            if(TextUtils.isEmpty(url1)){
                return;
            }


            String fileName = info.title + ".mp4";
            if("今日头条-今日头条".equals(info.title) || info.title.startsWith("http")){
                if(!TextUtils.isEmpty(titleFromIntent)){
                    fileName = titleFromIntent +".mp4";
                }

            }
            WebviewDownladListenerImpl.download(url1,fileName);


        } catch (Throwable throwable) {
            LogUtils.w(throwable);
            ToastUtils.showLong(throwable.getMessage());
        }
    }

    private void checkIfTouTiao(WebView view, String url) {
        String js = "var vi = document.getElementsByTagName(\"video\")[0]\n" +
                "      vi.addEventListener(\"play\",()=>{\n" +
                "        console.log(\"开始播放: src: \"+vi.getElementsByTagName(\"source\")[1].getAttribute(\"src\"));\n" +
                "        //console.log(\"开始播放: src0: \"+vi.getAttribute(\"src\"));\n" +
                "        //parent.replaceChild (newnode,oldnode ) ；\n" +
                "        var div = document.createElement(\"div\")\n" +
                "\n" +
                "        //div.style.position = \"relative\"\n" +
                "\n" +
                "        let htmlButtonElement = document.createElement(\"button\");\n" +
                "        htmlButtonElement.textContent = \"下载视频\"\n" +
                "        //htmlButtonElement.style.position = \"absolute\"\n" +
                "        //htmlButtonElement.style.zIndex = \"999\";\n" +
                "        div.appendChild(htmlButtonElement);\n" +
                "\n" +
                "        let br = document.createElement(\"br\");\n" +
                "        div.appendChild(br);\n" +
                "\n" +
                "        vi.parentNode.replaceChild(div,vi);\n" +
                "        div.appendChild(vi);\n" +
                "        //vi.style.zIndex = \"1\";\n" +
                "\n" +
                "        htmlButtonElement.addEventListener(\"click\",()=>{\n" +
                "          //alert(\"发起视频下载:\");\n" +
                "          window.location.href = vi.getElementsByTagName(\"source\")[1].getAttribute(\"src\");\n" +
                "        });";
        view.loadUrl("javascript:" + js);
    }


    String originalUrl = "";

    private void go(String url) {
        originalUrl = url;
        info.url = url;
        WebConfigger.syncCookie(webView, url);
        if (mAgentWeb == null) {
            LogUtils.w("mAgentWeb == null");
            //mAgentWeb = preAgentWeb.go(url);
        } else {
            mAgentWeb.getUrlLoader().loadUrl(url);
        }


    }


    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        //解决内存泄漏的问题
        sourceLoadListener = null;
    }

    public boolean onBackPressed() {
        if (mAgentWeb == null) {
            return false;
        }
        return mAgentWeb.back();
    }


}
