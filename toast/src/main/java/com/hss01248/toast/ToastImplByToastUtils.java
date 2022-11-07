package com.hss01248.toast;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;
import com.hjq.toast.CustomToast;
import com.hjq.toast.ToastParams;
import com.hjq.toast.ToastStrategy;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.config.IToastInterceptor;
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
        params.style = new CustomViewToastStyle(R.layout.notice_window);
        params.strategy = new ToastStrategy(){
            @Override
            public com.hjq.toast.config.IToast createToast(IToastStyle<?> style) {
                com.hjq.toast.config.IToast toast = super.createToast(style);
                if (toast instanceof CustomToast) {
                    CustomToast customToast = ((CustomToast) toast);
                    View view = customToast.getView();
                    if(view instanceof ViewGroup){
                        ViewGroup viewGroup = (ViewGroup) view;
                        ImageView ivIcon = viewGroup.findViewById(R.id.icon);
                        TextView tvMsg = viewGroup.findViewById(R.id.message);

                        ivIcon.setImageResource(R.drawable.ic_check_white_48dp);
                        tvMsg.setText(text);
                    }

                }
                return toast;
            }
        };
        params.strategy.registerStrategy(Utils.getApp());
        //params.text = text;
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
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0, SizeUtils.dp2px(30));
                return toast;
            }
        });
    }

    @Override
    public void error(CharSequence text) {
        checkInit();
        ToastParams params = new ToastParams();
        params.style = new CustomViewToastStyle(R.layout.notice_window);
        params.strategy = new ToastStrategy(){
            @Override
            public com.hjq.toast.config.IToast createToast(IToastStyle<?> style) {
                com.hjq.toast.config.IToast toast = super.createToast(style);
                if (toast instanceof CustomToast) {
                    CustomToast customToast = ((CustomToast) toast);
                    View view = customToast.getView();
                    if(view instanceof ViewGroup){
                        ViewGroup viewGroup = (ViewGroup) view;
                        ImageView ivIcon = viewGroup.findViewById(R.id.icon);
                        TextView tvMsg = viewGroup.findViewById(R.id.message);

                        //ivIcon.setImageResource(R.drawable.ic_clear_white_48dp);
                        tvMsg.setText(text);
                    }

                }
                return super.createToast(style);
            }
        };
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
        return null;
    }

    @Override
    public void dismissLoadingDialog(Dialog dialog) {

    }
}
