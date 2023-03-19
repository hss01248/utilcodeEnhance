package com.konstantinschubert.writeinterceptingwebview;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.just.agentweb.MiddlewareWebClientBase;
import com.just.agentweb.WebViewClientDelegate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public abstract class WriteHandlingWebViewClient extends MiddlewareWebClientBase {

    private final String MARKER = "AJAXINTERCEPT";
    private Map<String, String> ajaxRequestContents = new HashMap<>();



    public void addAjaxInterceptorJsInterface(WebView webView){
        AjaxInterceptJavascriptInterface ajaxInterface = new AjaxInterceptJavascriptInterface(this);
        webView.addJavascriptInterface(ajaxInterface, "interception");
    }

    /*
    This here is the "fixed" shouldInterceptRequest method that you should override.
    It receives a WriteHandlingWebResourceRequest instead of a WebResourceRequest.
     */
    public abstract WebResourceResponse shouldInterceptRequest(final WebView view, WriteHandlingWebResourceRequest request) ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final WebResourceResponse shouldInterceptRequest(
            final WebView view,
            WebResourceRequest request
    ) {

        String requestBody = null;
        Uri uri = request.getUrl();
        if (isAjaxRequest(request)) {
            requestBody = getRequestBody(request);
            uri = getOriginalRequestUri(request, MARKER);
        }
        WebResourceResponse webResourceResponse = shouldInterceptRequest(
                view,
                new WriteHandlingWebResourceRequest(request, requestBody, uri)
        );
        if (webResourceResponse == null) {
            return webResourceResponse;
        } else {
            return injectIntercept(uri,webResourceResponse, view.getContext());
        }
    }

    void addAjaxRequest(String id, String body) {
        ajaxRequestContents.put(id, body);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String getRequestBody(WebResourceRequest request) {
        String requestID = getAjaxRequestID(request);
        return getAjaxRequestBodyByID(requestID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isAjaxRequest(WebResourceRequest request) {
        return request.getUrl().toString().contains(MARKER);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String[] getUrlSegments(WebResourceRequest request, String divider) {
        String urlString = request.getUrl().toString();
        return urlString.split(divider);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String getAjaxRequestID(WebResourceRequest request) {
        return getUrlSegments(request, MARKER)[1];
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Uri getOriginalRequestUri(WebResourceRequest request, String marker) {
        String urlString = getUrlSegments(request, marker)[0];
        return Uri.parse(urlString);
    }

    private String getAjaxRequestBodyByID(String requestID) {
        String body = ajaxRequestContents.get(requestID);
        ajaxRequestContents.remove(requestID);
        return body;
    }

    private WebResourceResponse injectIntercept(Uri uri, WebResourceResponse response, Context context) {
        String encoding = response.getEncoding();
        String mime = response.getMimeType();
        InputStream responseData = response.getData();
        InputStream injectedResponseData = injectInterceptToStream(
                uri,
                context,
                responseData,
                mime,
                encoding
        );
        return new WebResourceResponse(mime, encoding, injectedResponseData);
    }

    private InputStream injectInterceptToStream(
            Uri uri, Context context,
            InputStream is,
            String mime,
            String charset
    ) {
        try {
            byte[] pageContents = Utils.consumeInputStream(is);
            if (mime.equals("text/html")) {
                pageContents = AjaxInterceptJavascriptInterface
                        .enableIntercept(context, pageContents)
                        .getBytes(charset);
            }

            return new ByteArrayInputStream(pageContents);
        } catch (Exception e) {
            LogUtils.w(uri.toString(),e.getMessage());
            throw new RuntimeException(e);
        }
    }
}