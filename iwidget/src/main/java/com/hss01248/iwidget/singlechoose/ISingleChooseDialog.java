package com.hss01248.iwidget.singlechoose;

import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Despciption todo
 * @Author hss
 * @Date 08/12/2022 09:41
 * @Version 1.0
 */
public interface ISingleChooseDialog {

   default void showAtBottom(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener){
       showAtBottom(title,null,datas,listener);
   }

   default void showInCenter(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener){
       showInCenter(title,null,datas,listener);
   }

    void showInPopMenu(View view,int checkedIndex, CharSequence[] datas, SingleChooseDialogListener listener);


    void showAtBottom(CharSequence title, @Nullable  String msg, CharSequence[] datas, SingleChooseDialogListener listener);

    void showInCenter(CharSequence title, @Nullable  String msg,CharSequence[] datas, SingleChooseDialogListener listener);


}
