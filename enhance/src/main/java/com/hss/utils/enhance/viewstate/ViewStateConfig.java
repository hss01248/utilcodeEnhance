package com.hss.utils.enhance.viewstate;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 10:30
 * @Version 1.0
 */
public class ViewStateConfig {
    public static ViewStateConfig globalConfig = new ViewStateConfig();

    public @LayoutRes int emptyLayout;
    public @LayoutRes int errorLayout;
    public @LayoutRes int loadingLayout;


    public String loadingMsg = "";
    public String emptyMsg = "";
    public @DrawableRes int emptyIcon = 0;
    public @DrawableRes int errorIcon = 0;

    public Runnable emptyClick = null;
    public Runnable errorClick = null;
}
