package com.hss.utilsenhance;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/13/24 4:47 PM
 * @Version 1.0
 */
public class ViewHolderDemoActivity extends AppCompatActivity {

    DemoViewHolder viewHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewHolder = new DemoViewHolder(this);
        setContentView(viewHolder.getRootView());

        viewHolder.init("请求成功");
    }





}
