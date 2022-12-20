package com.hss01248.utils.ext.intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.UrlEncodeUtil;

/**
 * @author: Administrator
 * @date: 2022/3/6
 * @desc:
 * 常用action  https://blog.csdn.net/u010687392/article/details/43899133
 * 匹配规则说明  https://blog.csdn.net/u011240877/article/details/71305797
 */
public class SysIntentShareDispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("获取到intent",getIntent());
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setBackground(new ColorDrawable(Color.WHITE));
        textView.setPadding(60,60,60,60);
        textView.setText(msg());
        textView.setTextColor(Color.BLACK);
        scrollView.addView(textView);

        setContentView(scrollView);
    }

    private String msg() {
        String str =  getIntent().toString();
        if(getIntent().getData() != null){
            str = str+"\ngetData(): "+ UrlEncodeUtil.decode(getIntent().getData().toString());
        }
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            return str;
        }
        str += "\n\nextras: "+ extras.size();
        for (String s : extras.keySet()) {
           str = str+ "\n"+s+" : "+ extras.get(s);
        }
        return str;
    }
}
