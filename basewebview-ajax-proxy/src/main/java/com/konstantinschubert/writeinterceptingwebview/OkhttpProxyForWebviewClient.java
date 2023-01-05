package com.konstantinschubert.writeinterceptingwebview;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Despciption todo
 * @Author hss
 * @Date 05/01/2023 17:13
 * @Version 1.0
 */
public class OkhttpProxyForWebviewClient extends WriteHandlingWebViewClient{

    OkHttpClient client;
    public OkhttpProxyForWebviewClient() {
        if(client == null){
            initDefaultClient();
        }
    }

    private OkHttpClient initDefaultClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followRedirects(false);
        builder.followSslRedirects(false);//测试: http://www.baidu.com
        builder.protocols(Arrays.asList(Protocol.HTTP_1_1));
        //OkhttpAspect.fixOkHttpBug(builder);
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WriteHandlingWebResourceRequest webResourceRequest) {
        String url = webResourceRequest.getUrl().toString();

        Request.Builder builder = new Request.Builder();

        Map<String, String> requestHeaders = webResourceRequest.getRequestHeaders();
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //构造请求
        builder.url(url);
        //.method(webResourceRequest.getMethod(), null);
        //body
        //https://github.com/KonstantinSchubert/request_data_webviewclient
        //webResourceRequest.

        if(webResourceRequest.getMethod().equals("GET")){
            builder.method(webResourceRequest.getMethod(), null);
        }else if("POST".equals(webResourceRequest.getMethod())){
            String ajaxData = webResourceRequest.getAjaxData();
            if(ajaxData == null || TextUtils.isEmpty(ajaxData)){
                //method POST must have a request body.
                builder.method(webResourceRequest.getMethod(), RequestBody.create(MediaType.parse("text/plain"),""));
            }else {
                //todo 流的怎么处理?
                builder.method(webResourceRequest.getMethod(), RequestBody.create(MediaType.parse("application/json"),ajaxData));
            }
        }else {
            String ajaxData = webResourceRequest.getAjaxData();
            if(ajaxData == null || TextUtils.isEmpty(ajaxData)){
                //method POST must have a request body.
                builder.method(webResourceRequest.getMethod(), null);
            }else {
                builder.method(webResourceRequest.getMethod(), RequestBody.create(MediaType.parse("application/json"),ajaxData));
            }
        }


        if(client == null){
            client =  initDefaultClient();
        }
        Call synCall = client.newCall(builder.build());
        okhttp3.Response response = null;
        WebResourceResponse webResourceResponse = null;
        try {
            response = synCall.execute();

            //MIME类型
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
            String contentType = response.headers().get("Content-Type");
            String encoding = "utf-8";
            //获取ContentType和编码格式
            if (contentType != null && !"".equals(contentType)) {
                if (contentType.contains(";")) {
                    String[] args = contentType.split(";");
                    mimeType = args[0];
                    String[] args2 = args[1].trim().split("=");
                    if (args.length == 2 && args2[0].trim().toLowerCase().equals("charset")) {
                        encoding = args2[1].trim();
                    }
                } else {
                    mimeType = contentType;
                }
            }


            //响应行和响应体
            webResourceResponse = new WebResourceResponse(mimeType, encoding,response.body() ==null
                    //todo body为空的情况
                    ? new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8))
                    : response.body().byteStream());
            String message = response.message();
            int code = response.code();
            if (TextUtils.isEmpty(message) && (code >= 200 && code<300)) {
                //message不能为空
                message = "OK";
            }
            webResourceResponse.setStatusCodeAndReasonPhrase(code, message);

            //响应头
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < response.headers().size(); i++) {
                //相应体的header
                map.put(response.headers().name(i), response.headers().value(i));
            }
            webResourceResponse.setResponseHeaders(map);


        } catch (Throwable e) {
            LogUtils.w(e);
            webResourceResponse = new WebResourceResponse("text/plain","utf-8",
                    //todo body为空的情况
                    new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
            webResourceResponse.setStatusCodeAndReasonPhrase(510, e.getClass().getSimpleName()+" : "+e.getMessage());
        }

        return webResourceResponse;
    }
}
