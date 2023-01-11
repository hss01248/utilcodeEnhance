package com.hss01248.basewebview.subwindow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.hss01248.basewebview.BaseQuickWebview;

/**
 * @Despciption todo
 * @Author hss
 * @Date 06/01/2023 14:19
 * @Version 1.0
 */
public class JsNewWindowFragment extends Fragment {

    public JsNewWindowFragment(BaseQuickWebview quickWebview) {
        this.quickWebview = quickWebview;
    }

    BaseQuickWebview quickWebview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        quickWebview.resetLifecycleOwner(this);



        configView(quickWebview);
        return quickWebview;
    }

    public static void configView(BaseQuickWebview quickWebview){
        ViewGroup.LayoutParams layoutParams1 = quickWebview.getLayoutParams();
        if(layoutParams1 == null){
            layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
        layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                //ActivityUtils.getTopActivity().findViewById(android.R.id.content).getMeasuredHeight();
        quickWebview.setLayoutParams(layoutParams1);


        /*ViewGroup.LayoutParams layoutParams = quickWebview.getChildAt(1).getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth();
        quickWebview.getChildAt(1).setLayoutParams(layoutParams);
        int dp45 = SizeUtils.dp2px(45)+ BarUtils.getStatusBarHeight();
        quickWebview.getChildAt(1).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //quickWebview.getChildAt(1).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                View childAt = quickWebview.getChildAt(1);
                if(childAt.getLeft() != 0){
                    childAt.setLeft(0);
                }
                LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) childAt.getLayoutParams();
                if(layoutParams1.topMargin != dp45){
                    layoutParams1.topMargin = dp45;
                    childAt.setLayoutParams(layoutParams1);
                }
            }
        });*/
    }
}
