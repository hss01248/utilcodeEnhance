package com.hss.utils.enhance;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/08/2022 11:59
 * @Version 1.0
 */
public class HomeMaintaner {

    static long lastClicked;

    public static void onBackPressed(Activity mainActivity,boolean mockClickHomeButton,@Nullable Runnable superBack){
       if(lastClicked ==0 || System.currentTimeMillis() - lastClicked > 4000){
           lastClicked = System.currentTimeMillis();
           ToastUtils.showShort(R.string.main_click_again_to_home);
           return;
       }
       lastClicked = System.currentTimeMillis();
       realBack(mainActivity,mockClickHomeButton,superBack);
    }

    private static void realBack(Activity activity, boolean mockClickHomeButton, Runnable superBack) {
        if(mockClickHomeButton){
            clickHomeButton(activity);
        }else {
            activity.finish();
        }
        lastClicked = 0;
        if(superBack != null){
            superBack.run();
        }
    }

    public static void clickHomeButton(Activity activity) {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            activity.startActivity(i);
        }catch (Throwable throwable){
           throwable.printStackTrace();
            activity.finish();
        }

    }
}
