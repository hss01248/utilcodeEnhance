package com.hss01248.basewebviewdemo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss01248.basewebview.BaseWebviewActivity;
import com.hss01248.utils.ext.lifecycle.AppFirstActivityOnCreateListener;
import com.hss01248.utils.ext.lifecycle.FirstActivityCreatedCallback;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/10/2023 18:51
 * @Version 1.0
 */
public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        regist();
    }

    private void regist() {
        FirstActivityCreatedCallback.addAppFirstActivityOnCreateListener(new AppFirstActivityOnCreateListener() {
            @Override
            public void onForegroundBackgroundChanged(Activity activity, boolean changeToBackground) {
                AppFirstActivityOnCreateListener.super.onForegroundBackgroundChanged(activity, changeToBackground);
                if(!changeToBackground){
                    ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //应用获取焦点后才能读取,否则无法读取
                            try{
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData primaryClip = clipboardManager.getPrimaryClip();
                                if(primaryClip!=null){
                                    String clipboardText = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                                    LogUtils.dTag("ClipboardMonitor", "Clipboard text changed: " + clipboardText);
                                    if(clipboardText.contains("https://v.douyin.com/")){
                                        BaseWebviewActivity.start(activity,clipboardText.substring(clipboardText.indexOf("https://v.douyin.com/")));
                                    }
                                }

                            }catch (Throwable e){
                                LogUtils.w(e);
                            }

                        }
                    },500);

                }
            }
        });
    }
}
