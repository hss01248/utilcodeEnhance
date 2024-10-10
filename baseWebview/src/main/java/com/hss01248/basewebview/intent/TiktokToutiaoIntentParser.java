package com.hss01248.basewebview.intent;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.hss01248.utils.ext.intent.IParseIntent;

/**
 * @Despciption todo
 * @Author hss
 * @Date 07/11/2023 17:20
 * @Version 1.0
 */
public class TiktokToutiaoIntentParser implements IParseIntent {
    @Override
    public boolean parseIntent(Intent intent, AppCompatActivity activity) {
        Bundle extras = intent.getExtras();
        if(extras == null){
            return false;
        }
        Object kdescription = extras.get("Kdescription");
        if(kdescription ==null){
            return false;
        }
        String text = kdescription.toString();
        if(!text.contains("https://m.toutiao.com/") && !text.contains("https://www.iesdouyin.com/")){
            LogUtils.d("有Kdescription,但不包含https://m.toutiao.com/或者www.iesdouyin.com",text);
            return false;
        }
        String url = "";
        String title = "";
        if(text.contains("https://m.toutiao.com/")){
            url = text.substring(text.indexOf("https://m.toutiao.com/"));
            title = text.substring(0,text.indexOf("https://m.toutiao.com/"));
        }else if(text.contains("https://www.iesdouyin.com/")){
            url = text.substring(text.indexOf("https://www.iesdouyin.com/"));
            title = text.substring(0,text.indexOf("https://www.iesdouyin.com/"));
        }else if(text.contains("https://v.douyin.com/")){
            url = text.substring(text.indexOf("https://v.douyin.com/"));
            title = text.substring(0,text.indexOf("https://v.douyin.com/"));
        }

        try {
            ReflectUtils.reflect("com.hss01248.basewebview.BaseWebviewActivity")
                    .method("start", activity, url,title);
            activity.finish();
            return true;
        }catch (Throwable throwable){
            LogUtils.w(throwable);
        }
        return false;
    }
}
