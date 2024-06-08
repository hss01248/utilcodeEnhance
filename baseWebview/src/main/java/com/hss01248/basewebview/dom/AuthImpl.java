package com.hss01248.basewebview.dom;

import android.content.DialogInterface;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;

import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;

public class AuthImpl extends MiddlewareWebClientBase {

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        //super.onReceivedHttpAuthRequest(view, handler, host, realm);
        if(handler.useHttpAuthUsernamePassword()) {
            String[] creds = view.getHttpAuthUsernamePassword(host,realm);
            if(creds!=null) {
                handler.proceed(creds[0],creds[1]);
                return;
            }
        }
        StyledDialog.buildNormalInput("需要登录", "请输入账号",
                "请输入密码", "", "", new MyDialogListener() {
            @Override
            public void onGetInput(CharSequence input1, CharSequence input2) {
                super.onGetInput(input1, input2);
                String name = input1.toString();
                String pass = input2.toString();
                view.setHttpAuthUsernamePassword(host,realm,name,pass);
                handler.proceed(name,pass);
            }

            @Override
            public void onFirst() {

            }

            @Override
            public void onSecond() {

            }
        }).show();

    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        super.onReceivedLoginRequest(view, realm, account, args);
    }
}
