package com.hss01248.iwidget.msg;


import android.app.Dialog;

import com.hss01248.iwidget.BaseDialogListener;


/**
 * @Despciption todo
 * @Author hss
 * @Date 09/12/2022 09:33
 * @Version 1.0
 */
public class AlertDialogImplByMmDialog implements IAlertDialog{
    @Override
    public Dialog showMsg(CharSequence title, CharSequence msg, CharSequence positiveBtnText, CharSequence negtiveBtnText, BaseDialogListener listener) {

       /* final boolean[] fromAction = {false};
        final boolean[] fromCancelBtn = {false};

        AlertDialog alertDialog = MMAlertDialog.showDialog(ActivityUtils.getTopActivity(),
                title.toString(),
                msg.toString(),
                negtiveBtnText.toString(),
                positiveBtnText.toString(),
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancel(false, false, true);
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirm();
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(true);
        return alertDialog;*/
        return null;

    }
}
