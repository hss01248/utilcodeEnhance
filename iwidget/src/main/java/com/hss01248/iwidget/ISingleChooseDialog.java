package com.hss01248.iwidget;

import android.view.View;

/**
 * @Despciption todo
 * @Author hss
 * @Date 08/12/2022 09:41
 * @Version 1.0
 */
public interface ISingleChooseDialog {

    void showAtBottom(CharSequence title,CharSequence[] datas,SingleChooseDialogListener listener);

    void showInCenter(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener);

    void showInPopMenu(View view, CharSequence[] datas, SingleChooseDialogListener listener);
}
