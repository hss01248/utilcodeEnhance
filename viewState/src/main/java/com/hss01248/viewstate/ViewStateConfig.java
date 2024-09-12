package com.hss01248.viewstate;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 10:30
 * @Version 1.0
 */
public class ViewStateConfig {
    public static ViewStateConfig getGlobalConfig() {
        return globalConfig;
    }

    public static void setGlobalConfig(ViewStateConfig globalConfig) {
        ViewStateConfig.globalConfig = globalConfig;
    }



    public static ViewStateConfig getGlobalSmallViewConfig() {
        return globalSmallViewConfig;
    }

    public static void setGlobalSmallViewConfig(ViewStateConfig globalSmallViewConfig) {
        ViewStateConfig.globalSmallViewConfig = globalSmallViewConfig;
    }

    static ViewStateConfig globalConfig  = new Builder()
            .loadingLayout(R.layout.pager_loading)
            .emptyLayout(R.layout.pager_empty)
            .errorLayout(R.layout.pager_error)
            .build();

    static ViewStateConfig globalSmallViewConfig  = new Builder()
            .loadingLayout(R.layout.small_view_loading)
            .emptyLayout(R.layout.small_view_place_holder)
            .errorLayout(R.layout.small_view_error)
            .build();

     @LayoutRes int emptyLayout;
     @LayoutRes int errorLayout;
     @LayoutRes int loadingLayout ;
    OnViewStateChangedListener listener;

    public int getEmptyLayout() {
        return emptyLayout;
    }

    public int getErrorLayout() {
        return errorLayout;
    }

    public int getLoadingLayout() {
        return loadingLayout;
    }

    public OnViewStateChangedListener getListener() {
        return listener;
    }

    public String getLoadingMsg() {
        return loadingMsg;
    }

    public String getEmptyMsg() {
        return emptyMsg;
    }

    public int getEmptyIcon() {
        return emptyIcon;
    }

    public int getErrorIcon() {
        return errorIcon;
    }

    public String getErrorBtnText() {
        return errorBtnText;
    }

    public String getEmptyBtnText() {
        return emptyBtnText;
    }

    public Runnable getEmptyClick() {
        return emptyClick;
    }

    public Runnable getErrorClick() {
        return errorClick;
    }

    String loadingMsg = "";
     String emptyMsg = "";

     @DrawableRes int emptyIcon = 0;
     @DrawableRes int errorIcon = 0;

     boolean darkMode ;

    public boolean isDarkMode() {
        return darkMode;
    }

    String errorBtnText = "";
    String emptyBtnText = "";
     Runnable emptyClick = null;
     Runnable errorClick = null;


    private ViewStateConfig(Builder builder) {
        emptyLayout = builder.emptyLayout;
        errorLayout = builder.errorLayout;
        loadingLayout = builder.loadingLayout;
        listener = builder.listener;
        loadingMsg = builder.loadingMsg;
        emptyMsg = builder.emptyMsg;
        emptyIcon = builder.emptyIcon;
        errorIcon = builder.errorIcon;
        darkMode = builder.darkMode;
        errorBtnText = builder.errorBtnText;
        emptyBtnText = builder.emptyBtnText;
        emptyClick = builder.emptyClick;
        errorClick = builder.errorClick;
    }

    public static Builder newBuilder(ViewStateConfig copy) {
        Builder builder = new Builder();
        builder.emptyLayout = copy.getEmptyLayout();
        builder.errorLayout = copy.getErrorLayout();
        builder.loadingLayout = copy.getLoadingLayout();
        builder.listener = copy.getListener();
        builder.loadingMsg = copy.getLoadingMsg();
        builder.emptyMsg = copy.getEmptyMsg();
        builder.emptyIcon = copy.getEmptyIcon();
        builder.errorIcon = copy.getErrorIcon();
        builder.darkMode = copy.isDarkMode();
        builder.errorBtnText = copy.getErrorBtnText();
        builder.emptyBtnText = copy.getEmptyBtnText();
        builder.emptyClick = copy.getEmptyClick();
        builder.errorClick = copy.getErrorClick();
        return builder;
    }


    public static final class Builder {
        private int emptyLayout = globalConfig == null ? 0 : globalConfig.emptyLayout;
        private int errorLayout = globalConfig == null ? 0 : globalConfig.errorLayout;
        private int loadingLayout = globalConfig == null ? 0 : globalConfig.loadingLayout;
        private OnViewStateChangedListener listener;
        private String loadingMsg = "";
        private String emptyMsg = "";
        private int emptyIcon;
        private int errorIcon;
        private boolean darkMode = false;
        private String errorBtnText;
        private String emptyBtnText;
        private Runnable emptyClick;
        private Runnable errorClick;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder emptyLayout(int val) {
            emptyLayout = val;
            return this;
        }

        public Builder errorLayout(int val) {
            errorLayout = val;
            return this;
        }

        public Builder loadingLayout(int val) {
            loadingLayout = val;
            return this;
        }

        public Builder listener(OnViewStateChangedListener val) {
            listener = val;
            return this;
        }

        public Builder loadingMsg(String val) {
            loadingMsg = val;
            return this;
        }

        public Builder emptyMsg(String val) {
            emptyMsg = val;
            return this;
        }

        public Builder emptyIcon(int val) {
            emptyIcon = val;
            return this;
        }

        public Builder errorIcon(int val) {
            errorIcon = val;
            return this;
        }

        public Builder darkMode(boolean darkMode) {
            this.darkMode = darkMode;
            return this;
        }

        public Builder errorBtnText(String val) {
            errorBtnText = val;
            return this;
        }

        public Builder emptyBtnText(String val) {
            emptyBtnText = val;
            return this;
        }

        public Builder emptyClick(Runnable val) {
            emptyClick = val;
            return this;
        }

        public Builder errorClick(Runnable val) {
            errorClick = val;
            return this;
        }

        public ViewStateConfig build() {
            return new ViewStateConfig(this);
        }
    }
}
