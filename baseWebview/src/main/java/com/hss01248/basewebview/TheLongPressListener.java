package com.hss01248.basewebview;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.hss.utils.enhance.TypeToDescUtil;
import com.hss.utils.enhance.intent.SysIntentUtil;
import com.hss01248.bigimageviewpager.LargeImageViewer;
import com.hss01248.bigimageviewpager.MyViewPager;
import com.hss01248.dialog.fullscreen.FullScreenDialog;
import com.hss01248.iwidget.BaseDialogListener;
import com.hss01248.iwidget.msg.AlertDialogImplByDialogUtil;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;
import com.hss01248.toast.MyToast;

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
        LogUtils.i(TypeToDescUtil.getDescByType(hitTestResult.getType(),WebView.HitTestResult.class),hitTestResult.getExtra());
        MyToast.debug(TypeToDescUtil.getDescByType(hitTestResult.getType(),WebView.HitTestResult.class)+"\n"+hitTestResult.getExtra());

        int type = hitTestResult.getType();
        switch (type) {
            case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                SysIntentUtil.dial(hitTestResult.getExtra());
                break;
            case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                break;
            case WebView.HitTestResult.GEO_TYPE: // TODO
                break;
            case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接, 也可能是广告
                break;
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: //带图片的超链接?
                //https://stackoverflow.com/questions/12168039/how-to-get-link-url-in-android-webview-with-hittestresult-for-a-linked-image-an
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        String url = (String) msg.getData().get("url");
                        String extra = hitTestResult.getExtra();
                        if(!TextUtils.isEmpty(extra) && extra.contains("data:image/") && extra.contains(";base64,")){
                            extra = "base64 img";
                        }
                        MyToast.debug("SRC_IMAGE_ANCHOR_TYPE: \n"+extra+"\ntarget url: \n"+ url);
                    }
                };
                Message message = handler.obtainMessage();
                quickWebview.getWebView().requestFocusNodeHref(message);

            case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
              showImgDialog(hitTestResult,v);
              return true;
            default:
                break;
        }
        return false;//保持长按可以复制文字

    }

    private void showImgDialog(WebView.HitTestResult hitTestResult,View v) {
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
                myViewPager.setBackgroundColor(Color.BLACK);
                List<String> ulrs = new ArrayList<>();
                ulrs.add(pic);
                ViewPager viewPager = LargeImageViewer.showBig(v.getContext(), myViewPager, ulrs, 0);

                FullScreenDialog dialog = new FullScreenDialog(v.getContext());
                dialog.setContentView(myViewPager);
                dialog.show();
                BarUtils.setStatusBarLightMode(dialog.getWindow(),false);
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
    }
}
