package com.hss01248.basewebview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.basewebview.databinding.TitlebarForWebviewBinding;
import com.hss01248.basewebview.search.WebSearchViewHolder;

/**
 * @Despciption todo
 * @Author hss
 * @Date 29/12/2022 16:23
 * @Version 1.0
 */
public class WebViewTitlebarHolder extends MyViewHolder<TitlebarForWebviewBinding,BaseQuickWebview> {
    public void setFullWebBrowserMode(boolean fullWebBrowserMode) {
        isFullWebBrowserMode = fullWebBrowserMode;
    }

    @Override
    protected TitlebarForWebviewBinding createBinding(ViewGroup parent) {
        return TitlebarForWebviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
    }

    /**
     * 一般是普通查看网页模式
     * 可以切换为浏览器模式-WebBrowserMode
     */
    boolean isFullWebBrowserMode = true;
    BaseQuickWebview quickWebview;
    public WebViewTitlebarHolder(ViewGroup parent) {
        super(parent);
    }

    @Override
    protected void assignDataAndEventReal(BaseQuickWebview data) {
        quickWebview = data;

        binding.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.showMenu();
            }
        });
        binding.ivRightRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.getWebView().reload();
            }
        });
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.getTopActivity().finish();
            }
        });

        binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFullWebBrowserMode){
                    //MyToast.debug("not isFullWebBrowserMode ");
                    return;
                }
                WebSearchViewHolder.showHistory(v.getContext(),data);
            }
        });
    }



}
