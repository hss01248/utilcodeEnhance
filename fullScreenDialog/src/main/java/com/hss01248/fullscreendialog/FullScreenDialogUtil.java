package com.hss01248.fullscreendialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.GsonBuilder;



import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 6/11/24 3:57 PM
 * @Version 1.0
 */
public class FullScreenDialogUtil {

    public static void showMap(String title,Map map){
        String text = new GsonBuilder().setPrettyPrinting().create().toJson(map);
        showText(title,text);
    }

    public static void showText(String title,String text){
        TextView textView = new TextView(ActivityUtils.getTopActivity());
        textView.setText(text);
        ScrollView scrollView = new ScrollView(ActivityUtils.getTopActivity());
        scrollView.addView(textView);
        int padding = SizeUtils.dp2px(5);
        textView.setPadding(padding,padding,padding,padding);

        showView(title, scrollView);

    }

    public static void showView(String title, ScrollView scrollView) {
        AlertDialog dialog = new AlertDialog.Builder(ActivityUtils.getTopActivity())
                .setTitle(title)
                .setView(scrollView)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog0) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dialog.getWindow().setBackgroundBlurRadius(20);
                }*/
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
                attributes.width = ScreenUtils.getScreenWidth();
                dialog.getWindow().setAttributes(attributes);
            }
        });
        dialog.show();
    }

    /**
     * 不支持沉浸式,不支持设置状态栏颜色,因为状态栏以上都是背景activity的内容
     * 如果需要设置,自行调用UltimateBarX的api,如果设置了,注意在dismiss和cancel时恢复原activity的窗口属性
     * @param view
     * @return
     */
    public static Dialog showFullScreen(View view){
        AlertDialog dialog = new AlertDialog.Builder(ActivityUtils.getTopActivity())
                .setView(view)
                .create();
       /* Dialog dialog = new Dialog(ActivityUtils.getTopActivity());
        dialog.setContentView(view);*/
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog0) {

                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
                attributes.width = height;
                attributes.height = height;
                dialog.getWindow().setAttributes(attributes);

                expandHeight( dialog,R.id.custom);
                expandHeight( dialog,R.id.parentPanel);

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = height;
                layoutParams.height = height;
                view.setLayoutParams(layoutParams);

                //状态栏不要变色






                /*BarUtils.transparentStatusBar(dialog.getWindow());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setNavigationBarColor(Color.WHITE);
                    dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
                    BarUtils.setNavBarColor(dialog.getWindow(),Color.WHITE);
                }*/
               /* ImmersionBar.with(ActivityUtils.getTopActivity(), dialog)
                        .navigationBarColorInt(Color.WHITE)
                        .navigationBarDarkIcon(true)
                        .init();*/


               // BarUtils.setNavBarLightMode(dialog.getWindow(),true);
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        /*dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImmersionBar.with(ActivityUtils.getTopActivity(), dialog).init();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog0) {
                ImmersionBar.destroy(ActivityUtils.getTopActivity(), dialog);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog0) {
                ImmersionBar.destroy(ActivityUtils.getTopActivity(), dialog);
            }
        });*/
        //FullScreenDialog.setDialogToFullScreen(dialog);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setStatusBarColor(Color.GREEN);//设置bar为透明色
        }*/

       /* Window window = dialog.getWindow();
         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//获取视口全屏大小
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //这个flag表示window负责绘制状态栏的背景当设置了这个flag,系统状态栏会变透明,同时这个相应的区域会被填满 getStatusBarColor() and getNavigationBarColor()的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);//设置bar为透明色
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setStatusBarContrastEnforced(false);
        }*/



        dialog.show();
        return dialog;
       /* dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog0) {
                ImmersionBar.destroy(ActivityUtils.getTopActivity(), dialog);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog0) {
                ImmersionBar.destroy(ActivityUtils.getTopActivity(), dialog);
            }
        });*/
    }

    private static void expandHeight( AlertDialog dialog, int id) {
        View custom = dialog.getWindow().findViewById(id);
        if(custom !=null){
            ViewGroup.LayoutParams layoutParams1 = custom.getLayoutParams();
            layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
            custom.setLayoutParams(layoutParams1);
        }else{
            LogUtils.w("no custom id");
        }
    }

    private static int screenHeight() {
        return ActivityUtils.getTopActivity().findViewById(android.R.id.content).getMeasuredHeight();
    }
}
