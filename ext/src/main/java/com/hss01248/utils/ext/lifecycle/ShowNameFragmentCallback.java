package com.hss01248.utils.ext.lifecycle;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;



/**
 * @Despciption todo
 * @Author hss
 * @Date 20/06/2022 10:12
 * @Version 1.0
 */
public class ShowNameFragmentCallback extends FragmentManager.FragmentLifecycleCallbacks{

    @Override
    public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f,
                                      @NonNull View v, @Nullable Bundle savedInstanceState) {
        if(v instanceof ViewGroup){
            ViewGroup root = (ViewGroup) v;
            TextView textView = new TextView(v.getContext());
            //textView.setId(R.id.tv_text_debug);
            int padding = SizeUtils.dp2px(2);
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(10);
            textView.setText( f.getClass().getSimpleName());
            textView.setPadding(padding,padding,padding,padding);
            try {
                if(root instanceof ScrollView){
                    root = (ViewGroup) root.getChildAt(0);
                }
                root.addView(textView);
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }

        }else {
            LogUtils.i("not view group, not add");
        }
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
    }

}
