package com.hss.utils.enhance;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.palette.graphics.Palette;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.List;

/**
 * @Despciption https://zhuanlan.zhihu.com/p/528604535
 * @Author hss
 * @Date 13/01/2023 14:06
 * @Version 1.0
 */
public class BarColorUtil  {

    public static void autoFitStatusBarLightModeWhenResume(FragmentActivity activity){
        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                autoFitStatusBarLightModeNow(activity.getWindow());
            }
        });
    }

    public static void autoFitStatusBarLightMode(Dialog dialog){
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog0) {
                autoFitStatusBarLightModeNow(dialog.getWindow());

            }
        });
    }

    public static void autoFitStatusBarLightModeNow(Window window) {
        View decorView = window.getDecorView();
        decorView.setDrawingCacheEnabled(true);
        Bitmap drawingCache = decorView.getDrawingCache();
        decorView.setDrawingCacheEnabled(false);
        ThreadUtils.executeByCpu(new ThreadUtils.SimpleTask<Integer>() {
            @Override
            public Integer doInBackground() throws Throwable {
                List<Palette.Swatch> swatches = Palette.from(drawingCache)
                        .setRegion(0, 0, ScreenUtils.getScreenWidth(), BarUtils.getStatusBarHeight())
                        .maximumColorCount(5).generate().getSwatches();
                if(swatches == null || swatches.isEmpty()){
                    return Color.WHITE;
                }
                Palette.Swatch swatchMax = null;
                for (Palette.Swatch swatch : swatches) {
                    if(swatchMax == null){
                        swatchMax =  swatch;
                    }else {
                        if(swatchMax.getPopulation() < swatch.getPopulation()){
                            swatchMax = swatch;
                        }
                    }
                }
                return swatchMax.getRgb();
            }

            @Override
            public void onSuccess(Integer result) {
                double luminance = ColorUtils.calculateLuminance(result);
                //BarUtils.setStatusBarLightMode(window, isLightColor(result));
                // 当luminance小于0.5时，我们认为这是一个深色值.
                if (luminance < 0.5) {
                    setDarkStatusBar(window);
                } else {
                    setLightStatusBar(window);
                }
            }
        });

    }
    public static boolean isLightColor(@ColorInt int color) {
        return 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color) >= 127.5;
    }

    public static void setLightStatusBar(Window window) {
        int flags = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void setDarkStatusBar(Window window) {
        int flags =  window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        window.getDecorView().setSystemUiVisibility(flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
