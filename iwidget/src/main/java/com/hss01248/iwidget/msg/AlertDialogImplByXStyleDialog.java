package com.hss01248.iwidget.msg;

import android.app.Dialog;

import com.hss01248.iwidget.BaseDialogListener;


/**
 * @Despciption todo
 * @Author hss
 * @Date 09/12/2022 09:33
 * @Version 1.0
 */
public class AlertDialogImplByXStyleDialog implements IAlertDialog{
    @Override
    public Dialog showMsg(CharSequence title, CharSequence msg, CharSequence positiveBtnText, CharSequence negtiveBtnText, BaseDialogListener listener) {

       /* final boolean[] fromAction = {false};
        final boolean[] fromCancelBtn = {false};

        AlertDialog alertDialog =  new AlertDialog(ActivityUtils.getTopActivity());
                //.setCancelable(false)
        alertDialog .setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog .setMessage(msg);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        alertDialog  .setLeftButton(negtiveBtnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(false,false,true);
            }
        }) ;//.setLeftButton("取消", null)
        alertDialog   .setRightButton(positiveBtnText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       listener.onConfirm();
                    }
                })
                .show();*/

        return null;

    }
}
