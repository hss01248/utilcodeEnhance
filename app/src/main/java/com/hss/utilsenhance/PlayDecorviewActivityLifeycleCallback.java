package com.hss.utilsenhance;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/06/2022 09:40
 * @Version 1.0
 */
public class PlayDecorviewActivityLifeycleCallback implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    //https://github.com/Petterpx/FloatingX
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        LogUtils.w("decorview",decorView);
        //extends FrameLayout
        if(decorView instanceof FrameLayout){
            FrameLayout root = (FrameLayout) decorView;
            TextView textView = new TextView(activity);
            int padding = SizeUtils.dp2px(24);
            textView.setText("activity: "+ activity);
            textView.setPadding(padding,padding,padding,padding);
            root.addView(textView);

            for (int i = 0; i < root.getChildCount(); i++) {
                //LogUtils.i("getChildAt:"+i,root.getChildAt(i));

            }
        }


    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
