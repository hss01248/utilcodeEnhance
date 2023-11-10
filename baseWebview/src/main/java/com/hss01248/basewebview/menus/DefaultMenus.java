package com.hss01248.basewebview.menus;

import com.blankj.utilcode.util.ActivityUtils;
import com.hss.utils.enhance.intent.ShareUtils;
import com.hss01248.basewebview.BaseQuickWebview;
import com.hss01248.basewebview.IShowRightMenus;
import com.hss01248.basewebview.history.db.MyDbUtil;
import com.hss01248.basewebview.history.ui.HistoryCollectVpHolder;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;
import com.hss01248.qrscan.ScanCodeActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/12/2022 18:06
 * @Version 1.0
 */
public class DefaultMenus implements IShowRightMenus {


    @Override
    public List<ISingleChooseItem<BaseQuickWebview>> addMenus(BaseQuickWebview quickWebview) {
        List<ISingleChooseItem<BaseQuickWebview>> menus = new ArrayList<>();
        menus.add(new ISingleChooseItem<BaseQuickWebview>() {
            @Override
            public String text() {
                return "扫码";
            }

            @Override
            public void onItemClicked(int position, BaseQuickWebview bean) {
                ScanCodeActivity.scanForResult(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        quickWebview.loadUrl(s);
                    }
                });
            }
        });
        menus.add(new ISingleChooseItem<BaseQuickWebview>() {
            @Override
            public String text() {
                return "切换为全功能浏览器模式";
            }

            @Override
            public void onItemClicked(int position, BaseQuickWebview bean) {
                quickWebview.getTitlebarHolder().setFullWebBrowserMode(true);
            }
        });
        menus.add(new ISingleChooseItem<BaseQuickWebview>() {
            @Override
            public String text() {
                return "分享";
            }

            @Override
            public void onItemClicked(int position, BaseQuickWebview bean) {
                ShareUtils.shareMsg(ActivityUtils.getTopActivity(),"分享到",
                        "网页分享: "+ quickWebview.getCurrentTitle(),
                        quickWebview.getCurrentUrl(), null);
            }
        });
        menus.add(new ISingleChooseItem<BaseQuickWebview>() {
            @Override
            public String text() {
                return "添加到收藏";
            }

            @Override
            public void onItemClicked(int position, BaseQuickWebview bean) {
                //bean.getCurrentTitle()
                //MyDbUtil.getDaoSession().insert()
                MyDbUtil.addCollect(bean.getCurrentTitle(),bean.getCurrentUrl(),"");
            }
        });
        menus.add(new ISingleChooseItem<BaseQuickWebview>() {
            @Override
            public String text() {
                return "查看收藏/历史记录";
            }

            @Override
            public void onItemClicked(int position, BaseQuickWebview bean) {
                //bean.getCurrentTitle()
                new HistoryCollectVpHolder(quickWebview.getContext()).assignDataAndEvent(quickWebview).showInFullScreenDialog();
            }
        });

        return menus;
    }
}
