package com.hss.utilsenhance;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss.utilsenhance.databinding.ActivityViewHolderDemoHttpStatusBinding;
import com.hss01248.toast.MyToast;
import com.hss01248.viewstate.StatefulLayout;

import java.util.Random;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 4:50 PM
 * @Version 1.0
 */
public class DemoViewHolder extends BaseViewHolder<ActivityViewHolderDemoHttpStatusBinding,String> {
    StatefulLayout stateManager;
    public DemoViewHolder(Context context) {
        super(context);

        stateManager = StatefulLayout.wrapWithStateOfPage(rootView, new Runnable() {
            @Override
            public void run() {
                init("重试后结果成功");
            }
        });
        rootView = stateManager;
    }



    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, String bean) {
        stateManager.showLoading("loading...xxxx");
        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(new Random().nextBoolean()){
                    stateManager.showContent();
                    binding.tvContent.setText(bean);
                    setShouldInterceptBackPressed(true);
                }else {
                    stateManager.showError("请求错误");
                    setShouldInterceptBackPressed(false);
                }

            }
        }, 2000);

    }


    @Override
    protected void onBackPressed2() {
        super.onBackPressed2();
        MyToast.show("点击了后退键2222");
    }
}
