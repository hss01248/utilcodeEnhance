package com.hss01248.iwidget.singlechoose;

import android.view.View;

import androidx.annotation.DrawableRes;

import java.util.List;

public interface ISingleChooseItem<T> {


    default @DrawableRes int icon(){
        return 0;
    }
    String text();

    default  boolean isSelected(){
        return false;
    }

    void  onItemClicked(int position, T bean);



     static <T> void showAsMenu(View targetView, List<ISingleChooseItem<T>> menus, T info){
        CharSequence[] strs = new CharSequence[menus.size()];
        int selectedIdx = -1;
        for (int i = 0; i < menus.size(); i++) {
            strs[i] = menus.get(i).text();
            if(menus.get(i).isSelected()){
                selectedIdx = i;
            }
        }
        new SingleChooseDialogImpl().showInPopMenu(targetView, selectedIdx, strs, new SingleChooseDialogListener() {
            @Override
            public void onItemClicked(int position, CharSequence text) {
                menus.get(position).onItemClicked(position,info);
            }
        });

       /* final FloatMenu floatMenu = new FloatMenu(targetView.getContext(), targetView);
        String[] desc = new String[menus.size()];
        for (int i = 0; i < menus.size(); i++) {
            desc[i] = menus.get(i).text();
        }
        floatMenu.items(desc);
        floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                menus.get(position).onMenuClicked(position,info);
            }
        });
        floatMenu.showAsDropDown(targetView);*/
    }
}
