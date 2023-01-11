package com.hss01248.basewebview;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.hss01248.bigimageviewpager.LargeImageViewer;
import com.hss01248.bigimageviewpager.MyViewPager;
import com.hss01248.dialog.fullscreen.FullScreenDialog;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/01/2023 10:53
 * @Version 1.0
 */
public class TheLongPressListener implements View.OnLongClickListener {

    public TheLongPressListener(BaseQuickWebview quickWebview) {
        this.quickWebview = quickWebview;
    }

    BaseQuickWebview quickWebview;
    @Override
    public boolean onLongClick(View v) {
        final WebView.HitTestResult hitTestResult = quickWebview.getWebView().getHitTestResult();
        // 如果是图片类型或者是带有图片链接的类型
        if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            String pic = hitTestResult.getExtra();//获取图片
            List<ISingleChooseItem<String>> items = new ArrayList<>();

            items.add(new ISingleChooseItem<String>() {
                @Override
                public String text() {
                    return "预览大图";
                }

                @Override
                public void onItemClicked(int position, String bean) {
                    MyViewPager myViewPager = new MyViewPager(v.getContext());
                    List<String> ulrs = new ArrayList<>();
                    ulrs.add(pic);
                    ViewPager viewPager = LargeImageViewer.showBig(v.getContext(), myViewPager, ulrs, 0);

                    FullScreenDialog dialog = new FullScreenDialog(v.getContext());
                    dialog.setContentView(myViewPager);
                    BarUtils.setStatusBarLightMode(dialog.getWindow(),false);
                    dialog.show();
                }
            });
            items.add(new ISingleChooseItem<String>() {
                @Override
                public String text() {
                    return "保存到相册";
                }

                @Override
                public void onItemClicked(int position, String bean) {
                    //ImageUtils.sa
                }
            });
            items.add(new ISingleChooseItem<String>() {
                @Override
                public String text() {
                    return "图片信息";
                }

                @Override
                public void onItemClicked(int position, String bean) {
                    new AlertDialogImplByDialogUtil().showMsg("图片信息", pic, "ok", "",
                            new BaseDialogListener() {
                                @Override
                                public void onConfirm() {
                                    BaseDialogListener.super.onConfirm();
                                }
                            });
                }
            });

            ISingleChooseItem.showInCenter(items,pic);
            return true;
        }
        return false;//保持长按可以复制文字

    }
}
