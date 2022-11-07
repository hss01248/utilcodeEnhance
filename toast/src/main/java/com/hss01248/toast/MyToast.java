package com.hss01248.toast;

import android.app.Dialog;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;


/**
 * @Despciption todo
 * @Author hss
 * @Date 07/11/2022 10:38
 * @Version 1.0
 */
public class MyToast {
   static IToast toastApi = new ToastImplByToastUtils();
    
    public  static void  success(CharSequence text) {
        toastApi.success(text);
    }

    
    public static void  error(CharSequence text) {
        toastApi.error(text);
    }

    
    public static void  show(CharSequence text) {
        toastApi.show(text);
    }

    
    public static void  debug(CharSequence text) {
        toastApi.debug(text+"\n"+StringUtils.getString(R.string.toasty_debug));
    }

    
    public Dialog showLoadingDialog(@Nullable String loadingText) {
        return toastApi.showLoadingDialog(TextUtils.isEmpty(loadingText)? StringUtils.getString(R.string.toast_common_loading) :loadingText);
    }

    
    public static void  dismissLoadingDialog(Dialog dialog) {
        toastApi.dismissLoadingDialog(dialog);
    }
}
