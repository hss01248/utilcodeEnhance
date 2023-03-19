package com.hss.utilsenhance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hss.utilsenhance.databinding.ActivityViewStateSmallviewBinding;
import com.hss01248.viewstate.StatefulLayout;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 17:42
 * @Version 1.0
 */
public class StateActivitySmallView extends AppCompatActivity {

    ActivityViewStateSmallviewBinding binding;
    StatefulLayout stateLayout1;
    StatefulLayout stateLayout2;
    StatefulLayout ivState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewStateSmallviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



         stateLayout1 = StatefulLayout.wrapWithStateOfSmallView(binding.rl1, new Runnable() {
            @Override
            public void run() {
                stateLayout1.showLoading();
            }
        });
        stateLayout1.showLoading();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                stateLayout1.showError("");
            }
        },2000);

        stateLayout2 = StatefulLayout.wrapWithStateOfSmallView(binding.rl2, new Runnable() {
            @Override
            public void run() {
                stateLayout2.showContent();
            }
        });
        stateLayout2.showLoading();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                stateLayout2.showError("");
            }
        },2000);

        //todo 内部是addview,所以view的顺序会随着写代码的顺序变化而变化,如何固定和原始顺序一致? 多加一层?或者按原始view的顺序来写代码?
        ivState = StatefulLayout.wrapWithStateOfSmallView(binding.iv, new Runnable() {
            @Override
            public void run() {
                ivState.showContent();
            }
        });
        ivState.showLoading();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ivState.showError("");
            }
        },2000);

        //doNet();

    }

    private void doNet() {
  /*      stateLayout.showLoading();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int state = new Random().nextInt(3) + 1;
                switch (state) {
                    case StatefulLayout.ERROR:
                        stateLayout.showError("稍候重试");
                        break;
                    case StatefulLayout.EMPTY:
                        stateLayout.showEmpty();
                        break;
                    case StatefulLayout.CONTENT:
                        stateLayout.showContent();
                }

            }
        }, 2000);*/
    }
}
