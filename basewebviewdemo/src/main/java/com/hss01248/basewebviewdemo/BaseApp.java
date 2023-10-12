package com.hss01248.basewebviewdemo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hjq.permissions.XXPermissions;
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
        XXPermissions.setScopedStorage(true);

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
                                    String url = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                                    LogUtils.dTag("ClipboardMonitor", "Clipboard text changed: " + url);
                                    if(TextUtils.isEmpty(url)){
                                        return;
                                    }
                                    if(!url.contains("https://v.douyin.com/")){
                                        return;

                                    }
                                    if(url.equals(SPStaticUtils.getString("video_cli"))){
                                        return;
                                    }
                                    String finalUrl = url;
                                    new AlertDialog.Builder(ActivityUtils.getTopActivity())
                                            .setTitle("视频自动下载")
                                            .setMessage("检测到有头条/抖音拷贝的链接,是否自动下载?")
                                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        BaseWebviewActivity.start(activity,finalUrl.substring(finalUrl.indexOf("https://v.douyin.com/")));
                                                        SPStaticUtils.put("video_cli",finalUrl);
                                                    }catch (Throwable throwable){
                                                        LogUtils.w(throwable);
                                                    }
                                                }
                                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create().show();






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
