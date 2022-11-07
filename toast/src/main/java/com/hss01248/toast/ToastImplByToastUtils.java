package com.hss01248.toast;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hjq.toast.ToastParams;
import com.hjq.toast.ToastStrategy;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.config.IToastStyle;
import com.hjq.toast.style.CustomViewToastStyle;


/**
 * @Despciption todo
 * @Author hss
 * @Date 07/11/2022 10:41
 * @Version 1.0
 */
public class ToastImplByToastUtils implements IToast {
    @Override
    public void success(CharSequence text) {
        checkInit();

        ToastParams params = new ToastParams();
        params.style = new CustomViewToastStyle(R.layout.notice_window_success,Gravity.CENTER);
        params.text = text;
        params.toastDuration = Toast.LENGTH_LONG;
        ToastUtils.show(params);
    }

    private void checkInit() {
        if(ToastUtils.isInit()){
            return;
        }
        ToastUtils.init(Utils.getApp(),new ToastStrategy(){
            @Override
            public com.hjq.toast.config.IToast createToast(IToastStyle<?> style) {
                com.hjq.toast.config.IToast toast =  super.createToast(style);
                if(style instanceof CustomViewToastStyle){

                }else {
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0, SizeUtils.dp2px(30));
                }
                //
                return toast;
            }
        });
    }

    @Override
    public void error(CharSequence text) {
        checkInit();
        ToastParams params = new ToastParams();
        params.style = new CustomViewToastStyle(R.layout.notice_window_error,Gravity.CENTER);
        params.text = text;
        params.toastDuration = Toast.LENGTH_LONG;
        ToastUtils.show(params);
    }

    @Override
    public void show(CharSequence text) {
        checkInit();
        ToastUtils.show(text);
    }

    @Override
    public void debug(CharSequence text) {
        checkInit();
        ToastUtils.debugShow(text);
    }

    @Override
    public Dialog showLoadingDialog(@Nullable String loadingText) {

        //todo 显示dialog
        try {
            Dialog dialog = new Dialog(ActivityUtils.getTopActivity());
            LinearLayout linearLayout = (LinearLayout) ActivityUtils.getTopActivity().getLayoutInflater().inflate(R.layout.notice_window_loading,
                    ActivityUtils.getTopActivity().findViewById(android.R.id.content),false);
           // LinearLayout linearLayout = (LinearLayout) View.inflate(ActivityUtils.getTopActivity(), R.layout.notice_window_loading,ActivityUtils.getTopActivity().findViewById(android.R.id.content));
            TextView textView = linearLayout.findViewById(R.id.message);
            if(!TextUtils.isEmpty(loadingText)){
                textView.setText(loadingText);
            }

            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if(Looper.getMainLooper() == Looper.myLooper()){
                dialog.setContentView(linearLayout);
                dialog.show();
            }else {


            }

            ThreadUtils.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    dialog.setContentView(linearLayout);
                    dialog.show();
                }
            });

            return dialog;
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return null;
        }



    }

    @Override
    public void dismissLoadingDialog(Dialog dialog) {
        if(dialog == null){
            return;
        }
        if(Looper.getMainLooper() == Looper.myLooper()){
            dialog.dismiss();
        }else {

            ThreadUtils.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }

    }
}
