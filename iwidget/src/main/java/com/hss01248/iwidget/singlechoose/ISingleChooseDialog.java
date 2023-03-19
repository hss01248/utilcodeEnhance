package com.hss01248.iwidget.singlechoose;

import android.view.View;

/**
 * @Despciption todo
 * @Author hss
 * @Date 08/12/2022 09:41
 * @Version 1.0
 */
public interface ISingleChooseDialog {

    void showAtBottom(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener);

    void showInCenter(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener);

    void showInPopMenu(View view,int checkedIndex, CharSequence[] datas, SingleChooseDialogListener listener);
}
