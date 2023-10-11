package com.hss01248.basewebview.video;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/10/2023 17:52
 * @Version 1.0
 */
public class ClipboardMonitorService extends Service {


    public static void startMonitor(){
        // 启动剪贴板监控服务
        try{
            Intent serviceIntent = new Intent(Utils.getApp(), ClipboardMonitorService.class);
            Utils.getApp().startService(serviceIntent);
        }catch (Throwable throwable){
            LogUtils.w(throwable);
        }
    }

    private ClipboardManager clipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;

    @Override
    public void onCreate() {
        super.onCreate();

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                // 处理剪贴板内容变化的逻辑
                String clipboardText = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                Log.d("ClipboardMonitor", "Clipboard text changed: " + clipboardText);
            }
        };

        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        clipboardManager.removePrimaryClipChangedListener(clipChangedListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

