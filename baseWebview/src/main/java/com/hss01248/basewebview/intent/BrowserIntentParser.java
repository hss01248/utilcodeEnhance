package com.hss01248.basewebview.intent;

import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.hss01248.basewebview.BaseWebviewActivity;
import com.hss01248.utils.ext.intent.IParseIntent;

import java.util.Set;

/**
 * @Despciption todo
 * @Author hss
 * @Date 07/11/2023 17:11
 * @Version 1.0
 */
public class BrowserIntentParser implements IParseIntent {
    @Override
    public boolean parseIntent(Intent intent, AppCompatActivity activity) {
        String url = intent.getDataString();
        if(!TextUtils.isEmpty(url) && url.startsWith("http")){
            BaseWebviewActivity.start(activity,intent.getDataString());
            activity.finish();
            return true;
        }
        return false;
    }
}
