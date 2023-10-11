package com.hss01248.utils.ext.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;


/**
 * @Despciption 这里有两个开关,可以控制activity和fragment的日志打印. 默认activity日志开启,fragment关闭
 * @Author hss
 * @Date 27/04/2022 10:12
 * @Version 1.0
 */
public class LogActivityCallback implements Application.ActivityLifecycleCallbacks {

    public static boolean logActivity = true;
    public static boolean showActivityNameAtDecorView = true;
    public static boolean showFragmentNameAtRootView = false;
    public static boolean logFragment = false;
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        try {
            if(logActivity){
                if(activity.getIntent() != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if(savedInstanceState != null){
                            LogUtils.w(activity,activity.getIntent(),activity.getReferrer(),savedInstanceState);
                        }else {
                            LogUtils.i(activity,activity.getIntent(),activity.getReferrer());
                        }
                        // //https://www.jianshu.com/p/648d67eee455  -->只有反射能使用
                        //                        //android-app://com.huawei.android.launcher launcher点击图标启动
                        //                        //android-app://com.android.shell  - android studio adb启动
                        //                        //android-app://com.aku.fbdemo  点击打开普通页面
                        //                        //android-app://com.aku.fbdemo  通知栏点击打开
                        //                        //android-app://com.android.packageinstaller
                    }else {
                        if(savedInstanceState != null){
                            LogUtils.w(activity,activity.getIntent(),savedInstanceState);
                        }else {
                            LogUtils.i(activity,activity.getIntent());
                        }
                    }
                    if(activity.getIntent().getSourceBounds() != null){
                        LogUtils.w("start from launcher",activity.getIntent().getSourceBounds());
                    }
                    if(!TextUtils.isEmpty(activity.getCallingPackage())
                    ){//!activity.getCallingPackage().equals(AppUtils.getAppPackageName())
                        LogUtils.w("activity.getCallingPackage()",activity.getCallingPackage());
                    }
                    if(activity.getCallingActivity() != null){
                        LogUtils.w("activity.getCallingActivity()",activity.getCallingActivity());
                    }
                }else {
                    if(savedInstanceState != null){
                        LogUtils.w(activity,savedInstanceState);
                    }else {
                        LogUtils.i(activity);
                    }
                }
            }
            if(showActivityNameAtDecorView){
                showActivityNameAtDecorView2(activity);
            }

            if(logFragment){
                registerFragmentLifecycleCallbacks(activity,new LogFragmentCalback());
            }
            if(showFragmentNameAtRootView){
                registerFragmentLifecycleCallbacks(activity,new ShowNameFragmentCallback());
            }
        }catch (Throwable throwable){
            LogUtils.d(throwable);
        }



    }

    private void showActivityNameAtDecorView2(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        //extends FrameLayout
        if(decorView instanceof FrameLayout){
            FrameLayout root = (FrameLayout) decorView;
            TextView textView = new TextView(activity);
            //textView.setId(R.id.tv_text_debug);
            int padding = SizeUtils.dp2px(25);
            textView.setTextColor(Color.BLUE);
            textView.setTextSize(12);
            textView.setText( activity.getClass().getSimpleName());
            textView.setPadding(padding,padding,padding,padding);
            root.addView(textView);
        }
    }


    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if(logActivity)  LogUtils.v(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(logActivity)  LogUtils.d(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if(logActivity) LogUtils.d(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if(logActivity) LogUtils.v(activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        if(logActivity) LogUtils.v(activity,outState);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if(logActivity) LogUtils.v(activity);
    }

    private void registerFragmentLifecycleCallbacks(Activity activity, FragmentManager.FragmentLifecycleCallbacks callbacks) {
        if(!(activity instanceof FragmentActivity)){
            return;
        }
        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        fragmentActivity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(callbacks,true);

    }
}
