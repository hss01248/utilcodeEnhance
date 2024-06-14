package com.hss01248.fullscreendialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
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
import com.gyf.immersionbar.ImmersionBar;

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

    public static  void showFullScreen(View view){
        AlertDialog dialog = new AlertDialog.Builder(ActivityUtils.getTopActivity())
                .setView(view)
                .create();
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


                BarUtils.transparentStatusBar(dialog.getWindow());
                //BarUtils.setStatusBarColor(dialog.getWindow(),Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //BarUtils.setNavBarColor(dialog.getWindow(),Color.WHITE);
                    dialog.getWindow().setNavigationBarColor(Color.WHITE);
                    dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
                    BarUtils.setNavBarColor(dialog.getWindow(),Color.WHITE);
                }
               /* ImmersionBar.with(ActivityUtils.getTopActivity(), dialog)
                        .navigationBarColorInt(Color.WHITE)
                        .navigationBarDarkIcon(true)
                        .init();*/


               // BarUtils.setNavBarLightMode(dialog.getWindow(),true);
            }
        });
        dialog.show();
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
        });
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
