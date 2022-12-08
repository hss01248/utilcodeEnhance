package com.hss01248.iwidget.singlechoose;


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss01248.iwidget.R;
import com.kongzue.dialogx.dialogs.PopMenu;
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.SimpleCallback;



/**
 * @Despciption todo
 * @Author hss
 * @Date 08/12/2022 09:55
 * @Version 1.0
 */
public class SingleChooseDialogImpl implements ISingleChooseDialog {
    @Override
    public void showAtBottom(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener) {
        showList(title, datas,true, listener);
    }

    private void showList(CharSequence title, CharSequence[] datas, boolean bottom, SingleChooseDialogListener listener) {
        String[] datas2 = new String[datas.length];
        for (int i = 0; i < datas2.length; i++) {
            datas2[i] = datas[i].toString();
        }
        final boolean[] fromAction = {false};
        final boolean[] fromClickOutside = {false};
        final boolean[] fromBackPressed = {false};
        XPopup.Builder builder = new XPopup.Builder(ActivityUtils.getTopActivity())
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);
                        //LogUtils.w("onDismiss");
                        boolean fromCancelButtonClicked = !fromAction[0] && !fromClickOutside[0] && !fromBackPressed[0];
                        if (!fromAction[0]) {
                            listener.onCancel(fromBackPressed[0], fromClickOutside[0], fromCancelButtonClicked);
                        }
                        listener.onDismiss(fromBackPressed[0], fromClickOutside[0], fromCancelButtonClicked, fromAction[0]);
                    }

                    @Override
                    public void onClickOutside(BasePopupView popupView) {
                        fromClickOutside[0] = true;
                        super.onClickOutside(popupView);
                        LogUtils.w("onClickOutside");
                    }

                    @Override
                    public boolean onBackPressed(BasePopupView popupView) {
                        fromBackPressed[0] = true;
                        LogUtils.w("onBackPressed");
                        return super.onBackPressed(popupView);
                    }
                });
        if(bottom){
              builder.asBottomList(title,datas2,
                    new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            fromAction[0] = true;
                            LogUtils.d(text+","+position);
                            listener.onItemClicked(position,text);
                        }
                    })
                    .show();
        }else {
             builder .asCenterList(title,datas2,
                    new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            fromAction[0] = true;
                            LogUtils.w(text+","+position);
                            listener.onItemClicked(position,text);
                        }
                    })
                    .show();
        }

    }

    @Override
    public void showInCenter(CharSequence title, CharSequence[] datas, SingleChooseDialogListener listener) {
        showList(title, datas,false, listener);
    }

    /**
     * https://github.com/kongzue/DialogX/wiki/%E4%B8%8A%E4%B8%8B%E6%96%87%E8%8F%9C%E5%8D%95-PopMenu
     * @param view
     * @param datas
     * @param listener
     */
    @Override
    public void showInPopMenu(View view,int checkedIndex, CharSequence[] datas, SingleChooseDialogListener listener) {
        final boolean[] fromAction = {false};
        //左右15dp*2+16sp*文字
        String max = "";
        for (CharSequence data : datas) {
            if(data.toString().length() > max.length()){
                max = data.toString();
            }
        }
        TextView textView = new TextView(view.getContext());
        textView.setTextSize(16);
        textView.setText(max);
        textView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int width = textView.getMeasuredWidth() + SizeUtils.dp2px(30);


        String finalMax = max;
        PopMenu menu =  PopMenu.build()
                .setMenuList( datas);
        if(checkedIndex >=0 && checkedIndex< datas.length){
             menu.setOnIconChangeCallBack(new OnIconChangeCallBack<PopMenu>(false) {
                 @Override
                 public int getIcon(PopMenu dialog, int index, String menuText) {
                     return index == checkedIndex ? R.drawable.icon_checked_addr : R.drawable.icon_unchecked_addr;
                 }
             });
            width = width + SizeUtils.dp2px(35+8);
            //menu.setWidth(ScreenUtils.getScreenWidth());
        }

        int finalWidth = width;
        LogUtils.w("view width: " + view.getMeasuredWidth() + ", max Length text: " + finalWidth + ",text: " + finalMax);

        menu.setBaseView(view);
        if (finalWidth > view.getMeasuredWidth() ) {
            //&& !(checkedIndex >=0 && checkedIndex< datas.length)
            menu.setWidth(finalWidth);
        }
        menu
                .setOverlayBaseView(false)
                //.setWidth(ScreenUtils.getScreenWidth())
                //.setAlignGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .setDialogLifecycleCallback(new DialogLifecycleCallback<PopMenu>() {
                    @Override
                    public void onShow(PopMenu dialog) {
                        super.onShow(dialog);
                        listener.onShow(dialog);
                        //确定最大长度:
                        //dialog.getMenuTextInfo()
                        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ViewGroup menuView = dialog.getDialogView().findViewById(R.id.listMenu);
                                for (int i = 0; i < menuView.getChildCount(); i++) {
                                    ViewGroup viewGroup = (ViewGroup) menuView.getChildAt(i);
                                    View viewById = viewGroup.findViewById(R.id.space_dialogx_right_padding);
                                    if(viewById != null){
                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewById.getLayoutParams();
                                        layoutParams.leftMargin = 0;
                                        layoutParams.width =0;
                                        viewById.setLayoutParams(layoutParams);
                                    }
                                }
                            }
                        },300);

                    }

                    @Override
                    public void onDismiss(PopMenu dialog) {
                        super.onDismiss(dialog);
                        //LogUtils.w("onDismiss");
                        //todo backpressed,outsideclick无法区分
                        if (!fromAction[0]) {
                            listener.onCancel(false, true, false);
                        }
                        listener.onDismiss(false, false, false, fromAction[0]);
                    }
                })
                .setOnMenuItemClickListener(new OnMenuItemClickListener<PopMenu>() {
                    @Override
                    public boolean onClick(PopMenu dialog, CharSequence text, int index) {
                        fromAction[0] = true;
                        listener.onItemClicked(index, text);
                        return false;
                    }
                });
        menu.show();
    }

}
