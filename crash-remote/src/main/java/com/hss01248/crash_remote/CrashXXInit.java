package com.hss01248.crash_remote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.startup.Initializer;

import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.utils.ext.PkgListFetcher;
import com.hss01248.utils.ext.lifecycle.AppFirstActivityOnCreateListener;
import com.hss01248.utils.ext.lifecycle.BackgroundAndFirstActivityCreatedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 09/05/2022 14:41
 * @Version 1.0
 */
public class CrashXXInit implements Initializer<String> {

    public static  boolean crash = false;
    @Override
    public String create(Context context) {

        BackgroundAndFirstActivityCreatedCallback.addAppFirstActivityOnCreateListener(new AppFirstActivityOnCreateListener() {
            @Override
            public void onFirtActivityCreated(Activity activity, @Nullable Bundle savedInstanceState) {
                AppFirstActivityOnCreateListener.super.onFirtActivityCreated(activity, savedInstanceState);
                fetchInfo();
            }

            @Override
            public void onForegroundBackgroundChanged(Activity activity, boolean changeToBackground) {
                AppFirstActivityOnCreateListener.super.onForegroundBackgroundChanged(activity, changeToBackground);

                boolean isForResult = PkgListFetcher.isForResult(activity);
                if(isForResult){
                    return;
                }
                if (changeToBackground) {
                    fetchInfo();
                    return;
                }

            }
        });
        return "CrashXXInit";
    }


    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }


    private void fetchInfo() {

        PkgListFetcher.fetch("crashxx",
                "https://oss-kodo.hss01248.tech/2024/xxlist.json",
                new MyCommonCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        crash = aBoolean;
                        if(aBoolean){
                            ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int i = 1/0;
                                }
                            },1000);
                        }
                    }
                });

    }

}
