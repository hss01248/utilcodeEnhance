package com.hss01248.basewebview;

import static com.blankj.utilcode.util.ProcessUtils.getCurrentProcessName;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.basewebview.dom.JsCreateNewWinImpl;
import com.hss01248.basewebview.download.WebviewDownladListenerImpl;
import com.hss01248.basewebview.intent.BrowserIntentParser;
import com.hss01248.basewebview.intent.TiktokToutiaoIntentParser;
import com.hss01248.utils.ext.intent.SysIntentShareDispatcherActivity;

public class WebConfigger {

    public static WebviewInit getInit() {
        return init;
    }

    static WebviewInit init = new DefaultWebConfig();

    public static void init(WebviewInit init){
        if(init != null){
            WebConfigger.init = init;
        }
        SysIntentShareDispatcherActivity.addParser(new TiktokToutiaoIntentParser());
        SysIntentShareDispatcherActivity.addParser(new BrowserIntentParser());

    }




    static void config(WebView webView){
        init(webView);
        setDownloader(webView);
        setUA(webView);
    }

    /**
     * agentweb什么辣鸡玩意,直接被谷歌屏蔽ua
     * Mozilla/5.0 (Linux; Android 12; M2102K1C Build/SKQ1.211006.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/109.0.5414.117 Mobile Safari/537.36 AgentWeb/5.0.0  UCBrowser/11.6.4.950
     * @param webView
     */
    private static void setUA(WebView webView) {
 /*       String userAgentString = webView.getSettings().getUserAgentString();
        userAgentString = userAgentString.replace(AbsAgentWebSettings.USERAGENT_AGENTWEB,"")
                .replace(AbsAgentWebSettings.USERAGENT_UC,"")
                .replace(AbsAgentWebSettings.USERAGENT_QQ_BROWSER,"");*/
        String userAgentString =  new WebView(webView.getContext()).getSettings().getUserAgentString();
        LogUtils.i("normal userAgentString: "+ userAgentString);
        //if(WebDebugger.debug){
        userAgentString = userAgentString+" "+ AppUtils.getAppName()+"/"+AppUtils.getAppVersionName()+"/"+AppUtils.getAppVersionCode();
        LogUtils.i("normal userAgentString2: "+ userAgentString);
        //}

        webView.getSettings().setUserAgentString(userAgentString);
        //System.getProperty("http.agent");

        //Mozilla/5.0 (Linux; Android 12; M2102K1C Build/SKQ1.211006.001; wv) AppleWebKit/537.36 (KHTML, like Gecko)
        // Version/4.0 Chrome/109.0.5414.117 Mobile Safari/537.36

        //Mozilla/5.0 (Linux; Android 10; HRY-AL00a Build/HONORHRY-AL00a) AppleWebKit/537.36 (KHTML, like Gecko)
        // Chrome/80.0.3987.99 Mobile Safari/537.36 webviewdemo/4.5.8-debug rnBundleVersion/


    }

    private static void setDownloader(WebView webView) {
        webView.setDownloadListener(new WebviewDownladListenerImpl());
    }

    public static void syncCookie(WebView webView, String url) {
        CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //cookieManager.removeSessionCookie();// 移除旧的[可以省略]
        /*List<HttpCookie> cookies = new PersistentCookieStore(context).getCookies();// 获取Cookie[可以是其他的方式获取]
        for (int i = 0; i < cookies.size(); i++) {
            HttpCookie cookie = cookies.get(i);
            String value = cookie.getName() + "=" + cookie.getValue();
            cookieManager.setCookie(url, value);
        }*/
        CookieSyncManager.getInstance().sync();// To get instant sync instead of waiting for the timer to trigger, the host can call this.
    }

    static void init(WebView webView) {
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setDefaultTextEncodingName("utf-8");//字符编码UTF-8
        //支持获取手势焦点，输入用户名、密码或其他
        webView.requestFocusFromTouch();

        mWebSettings.setMediaPlaybackRequiresUserGesture(true);//SDK>18 是否支持手势控制网页媒体，比如视频的全屏

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // mWebSettings.setSafeBrowsingEnabled(false);// 是否开启安全模式
        }
        JsCreateNewWinImpl.enableMultipulWindow(webView,true);

        mWebSettings.setSupportZoom(true);//支持缩放
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);//但是不显示丑陋的缩放按钮
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //mWebSettings.setTextZoom(100);
        //设置自适应屏幕，两者合用
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webView的大小
        // //第一个方法设置webview推荐使用的窗口，设置为true。第二个方法是设置webview加载的页面的模式，也设置为true。
        //        //这方法可以让你的页面适应手机屏幕的分辨率，完整的显示在屏幕上，可以放大缩小。

        mWebSettings.setNeedInitialFocus(true); //当webView调用requestFocus时为webView设置节点

        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }



        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);//支持javascript

        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染等级

        mWebSettings.setEnableSmoothTransition(true);
        webView.setFitsSystemWindows(true);
        //缓存数据 (localStorage)
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置

        /*if(WebDebugger.debug){
            mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }else {
            if (NetworkUtils.isConnected()) {
                mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
            } else {
                mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
            }
        }*/

        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setSaveFormData(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        //setAppCacheEnabled --> android33没有这个api了
        /*mWebSettings.setAppCacheEnabled(true);
        String appCachePath = webView.getContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);*/
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法

        //因为Android P 行为变更，多进程 webView 不能使用同一个目录，需要为不同进程 webView 设置不同目录。为不同进程设置不同的目录即可解决问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            boolean isMainProcess = Utils.getApp().getPackageName().equals(getCurrentProcessName());
            if (!isMainProcess) {
                WebView.setDataDirectorySuffix("any-folder-name");
            }
        }



        // for remote debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(WebDebugger.debug);
        }

    }


}
