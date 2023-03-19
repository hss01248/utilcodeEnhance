package com.hss01248.iwidget;

import com.blankj.utilcode.util.LogUtils;

public interface BaseDialogListener {

    default void onShow(Object dialog){
        LogUtils.v("on show : "+ dialog);
    }

    default void onCancel(boolean fromBackPressed,boolean fromOutsideClick,boolean fromCancelButton){
        LogUtils.d("fromBackPressed-"+fromBackPressed+",fromOutsideClick-"+fromOutsideClick+",fromCancelButton-"+fromCancelButton);
    }

    default  void onDismiss(boolean fromBackPressed,boolean fromOutsideClick,boolean fromCancelButton,boolean fromAction){
        LogUtils.d("fromBackPressed-"+fromBackPressed+",fromOutsideClick-"+fromOutsideClick+",fromCancelButton-"+fromCancelButton+",fromAction-"+fromAction);
    }

    default  void onConfirm(){
        LogUtils.i("onConfirm : ");
    }
}
