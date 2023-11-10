package com.hss.utils.enhance;

import android.net.Uri;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @Despciption todo 解决崩溃,以及加号问题  https://blog.csdn.net/feinifi/article/details/83622606
 *
 * 规范(RFC 2396，定义URI)里, URI里的保留字符都需转义成%HH格式
 * 空格会被编码成%20，加号+本身也作为保留字而被编成%2B
 * @Author hss
 * @Date 24/02/2022 09:56
 * @Version 1.0
 */
public class UrlEncodeUtil {

    public static String encode(String str){
        try {
            String encoded = URLEncoder.encode(str, Charset.defaultCharset().displayName());
            //LogUtils.d("encode1",encoded);
            encoded = encoded.replaceAll("\\+", "%20");
           // LogUtils.d("encode2",encoded);
            return encoded;
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return str;
        }
    }

    public static String decode(String str){
        try {
            String encoded = URLDecoder.decode(str, Charset.defaultCharset().displayName());
            //LogUtils.d("decode1",encoded);
            encoded = encoded.replaceAll("%20", "\\+");
           // LogUtils.d("decode2",encoded);
            return encoded;
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return str;
        }
    }

    public static String getDecodedPath(String url){
        try {
            Uri uri = Uri.parse(url);
            LogUtils.i(uri.getPath(),uri.getEncodedPath());
            return uri.getPath();
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return url;
        }
    }

    public static String encodeFilePathToUrlUsage(String filePath){
        try {
            if(TextUtils.isEmpty(filePath)){
                return "";
            }
            //window路径方向
            filePath = filePath.replaceAll("\\\\","/");
            filePath = filePath.replaceAll("//","/");
            if(!filePath.contains("/")){
                return filePath;
            }
            String[] split = filePath.split("/");
            StringBuilder sb = new StringBuilder("/");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if(!TextUtils.isEmpty(s)){
                    sb.append(encode(s));
                    if(i != split.length){
                        sb.append("/");
                    }
                }
            }
            String str = sb.toString();
            if(str.endsWith("/")){
                str = str.substring(0,str.length()-1);
            }
            LogUtils.d(filePath,str);
            return str;

        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return filePath;
        }
    }


}
