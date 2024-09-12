package com.hss01248.basewebview.dom;

import android.content.DialogInterface;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hss01248.cipher.sp.EnSpUtil;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;

import java.nio.charset.StandardCharsets;

public class AuthImpl extends MiddlewareWebClientBase {

    static void save(String host,String realm,String name,String pw){
        String key = "web-basic-name-"+host+"-"+realm;
        SPStaticUtils.put(key,name);
        SPStaticUtils.put(key+"-pw", EncodeUtils.base64Encode2String(pw.getBytes()));
    }
    public static String[] getHttpAuthUsernamePassword(String host, String realm){
        String key = "web-basic-name-"+host+"-"+realm;
        String name = SPStaticUtils.getString(key);
        if(StringUtils.isEmpty(name)){
            return null;
        }
        String pw = SPStaticUtils.getString(key+"-pw","");
        pw = new String(EncodeUtils.base64Decode(pw));
        return new String[]{name,pw};
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        //
        if(handler.useHttpAuthUsernamePassword()) {
            String[] creds = view.getHttpAuthUsernamePassword(host,realm);
            if(creds!=null) {
                handler.proceed(creds[0],creds[1]);
                save(host,realm,creds[0],creds[1]);
                return;
            }
            StyledDialog.buildNormalInput("需要登录", "请输入账号",
                    "请输入密码", "", "", new MyDialogListener() {
                        @Override
                        public void onGetInput(CharSequence input1, CharSequence input2) {
                            super.onGetInput(input1, input2);
                            String name = input1.toString();
                            String pass = input2.toString();
                            view.setHttpAuthUsernamePassword(host,realm,name,pass);
                            save(host,realm,name,pass);
                            handler.proceed(name,pass);
                        }

                        @Override
                        public void onFirst() {

                        }

                        @Override
                        public void onSecond() {

                        }
                    }).show();
        }else{
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        super.onReceivedLoginRequest(view, realm, account, args);
    }
}
