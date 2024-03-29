package com.hss01248.viewstate;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public interface IViewState {

    void showLoading(@Nullable  CharSequence msg);
    void showEmpty(@Nullable  CharSequence msg,@Nullable @DrawableRes int icon,@Nullable  CharSequence btnText,@Nullable Runnable emptyClick);
    void showError(@Nullable  CharSequence msg,@Nullable @DrawableRes int icon, @Nullable  CharSequence btnText,@Nullable Runnable errorClick);
    void showContent();

    default void showLoading(){
        showLoading("");
    }
    default void showEmpty(){
        showEmpty("",0,null,null);
    }
    default void showError(String msg){
        showError(msg,0,null,null);
    }

}
