package com.hss01248.media.pick;

import com.blankj.utilcode.util.LogUtils;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;

/**
 * @Despciption todo
 * @Author hss
 * @Date 07/12/2022 19:48
 * @Version 1.0
 */
public abstract class MoreSimpleCallback  extends SimpleCallback {

    @Override
    public void onClickOutside(BasePopupView popupView) {
        super.onClickOutside(popupView);
        onCancel(popupView);
        LogUtils.w("onClickOutside");
    }

    @Override
    public boolean onBackPressed(BasePopupView popupView) {
        LogUtils.w("onBackPressed");
        onCancel(popupView);
        return super.onBackPressed(popupView);
    }

    public void onCancel(BasePopupView popupView){

    }


}
