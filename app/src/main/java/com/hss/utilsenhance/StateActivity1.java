package com.hss.utilsenhance;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hss.utilsenhance.databinding.ActivityViewStateBinding;
import com.hss01248.viewstate.StatefulLayout;
import com.hss01248.viewstate.ViewStateConfig;

import java.util.Random;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/11/2022 17:42
 * @Version 1.0
 */
public class StateActivity1 extends AppCompatActivity {

    ActivityViewStateBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewStateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.stateLayout.setConfig(ViewStateConfig.Builder
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
        binding.stateLayout.showLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int state = new Random().nextInt(3) + 1;
                switch (state) {
                    case StatefulLayout.ERROR:
                        binding.stateLayout.showError("稍候重试");
                        break;
                    case StatefulLayout.EMPTY:
                        binding.stateLayout.showEmpty();
                        break;
                    case StatefulLayout.CONTENT:
                        binding.stateLayout.showContent();
                }
            }
        }, 2000);
    }
}
