package com.hss01248.utils.ext.intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

/**
 * @author: Administrator
 * @date: 2022/3/6
 * @desc: //todo
 */
public class SysIntentShareDispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("获取到intent",getIntent());
        TextView textView = new TextView(this);
        textView.setBackground(new ColorDrawable(Color.GRAY));
        textView.setPadding(60,60,60,60);
        textView.setText(getIntent()+"");
        setContentView(textView);
    }
}
