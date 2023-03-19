package com.hss01248.iwidget.msg;

import android.app.Dialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.hss01248.iwidget.BaseDialogListener;

/**
 * @Despciption todo
 * @Author hss
 * @Date 09/12/2022 09:33
 * @Version 1.0
 */
public class AlertDialogImplByDialogUtil implements IAlertDialog{
    @Override
    public Dialog showMsg(CharSequence title, CharSequence msg, CharSequence positiveBtnText, CharSequence negtiveBtnText, BaseDialogListener listener) {
        StyledDialog.init(Utils.getApp());
        final boolean[] fromAction = {false};
        final boolean[] fromCancelBtn = {false};
       return StyledDialog.buildIosAlert(title, msg, new MyDialogListener() {
           @Override
           public void onShow() {
               super.onShow();
               listener.onShow(null);
           }

           @Override
           public void onDismiss() {
               super.onDismiss();
               listener.onDismiss(false,false, fromCancelBtn[0], fromAction[0]);
           }

           @Override
           public void onCancle() {
               super.onCancle();
               listener.onCancel(false,false,true);
           }

           @Override
            public void onFirst() {
               fromAction[0] = true;
                listener.onConfirm();

            }

            @Override
            public void onSecond() {
                fromCancelBtn[0]= true;
                listener.onCancel(false,false,true);

            }
        }).setBtnText(positiveBtnText,negtiveBtnText)
               .setActivity(ActivityUtils.getTopActivity())
               .show();

    }
}
