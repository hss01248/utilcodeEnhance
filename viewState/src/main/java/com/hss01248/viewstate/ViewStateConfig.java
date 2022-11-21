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

    static ViewStateConfig globalConfig  = new ViewStateConfig.Builder()
            .loadingLayout(R.layout.pager_loading)
            .emptyLayout(R.layout.pager_empty)
            .errorLayout(R.layout.pager_error)
            .build();

     @LayoutRes int emptyLayout;
     @LayoutRes int errorLayout;
     @LayoutRes int loadingLayout ;


     String loadingMsg = "";
     String emptyMsg = "";

     @DrawableRes int emptyIcon = 0;
     @DrawableRes int errorIcon = 0;

    String errorBtnText = "";
    String emptyBtnText = "";
     Runnable emptyClick = null;
     Runnable errorClick = null;


    private ViewStateConfig(Builder builder) {
        emptyLayout = builder.emptyLayout;
        errorLayout = builder.errorLayout;
        loadingLayout = builder.loadingLayout;
        loadingMsg = builder.loadingMsg;
        emptyMsg = builder.emptyMsg;
        emptyIcon = builder.emptyIcon;
        errorIcon = builder.errorIcon;
        emptyClick = builder.emptyClick;
        errorClick = builder.errorClick;
    }


    public static final class Builder {
        private int emptyLayout =  globalConfig == null ? 0 : globalConfig.emptyLayout;
        private int errorLayout = globalConfig == null ? 0 :globalConfig.errorLayout;
        private int loadingLayout = globalConfig == null ? 0 :globalConfig.loadingLayout;
        private String loadingMsg;
        private String emptyMsg;
        private int emptyIcon;
        private int errorIcon;
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
