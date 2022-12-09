package com.hss01248.iwidget.msg;

import android.app.Dialog;

import com.hss01248.iwidget.BaseDialogListener;

public interface IAlertDialog {

    Dialog showMsg(CharSequence title, CharSequence msg, CharSequence positiveBtnText, CharSequence negtiveBtnText, BaseDialogListener listener);
}
