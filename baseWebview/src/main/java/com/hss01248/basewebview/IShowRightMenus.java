package com.hss01248.basewebview;

import android.webkit.WebView;

import com.hss01248.iwidget.singlechoose.ISingleChooseItem;

import java.util.List;


public interface IShowRightMenus {

    List<ISingleChooseItem<BaseQuickWebview>> addMenus(BaseQuickWebview quickWebview);
}
