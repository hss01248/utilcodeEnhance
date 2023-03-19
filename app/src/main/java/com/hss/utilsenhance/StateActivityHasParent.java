package com.hss.utilsenhance;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hss.utilsenhance.databinding.ActivityViewStateHasParentBinding;
import com.hss01248.viewstate.StatefulLayout;
import com.hss01248.viewstate.ViewStateConfig;

import java.util.Random;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 17:42
 * @Version 1.0
 */
public class StateActivityHasParent extends AppCompatActivity {

    ActivityViewStateHasParentBinding binding;
    StatefulLayout stateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewStateHasParentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        stateLayout = StatefulLayout.wrapWithState(binding.llRoot,
                ViewStateConfig.Builder
                .newBuilder()
                .errorClick(new Runnable() {
                    @Override
                    public void run() {
                        doNet();
                    }
                }).build());

        doNet();

    }

    private void doNet() {
        stateLayout.showLoading();

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
        }, 2000);
    }
}
