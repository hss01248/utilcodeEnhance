package com.hss01248.utils.ext.intent;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.hss.utils.enhance.UrlEncodeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @date: 2022/3/6
 * @desc:
 * 常用action  https://blog.csdn.net/u010687392/article/details/43899133
 * 匹配规则说明  https://blog.csdn.net/u011240877/article/details/71305797
 */
public class SysIntentShareDispatcherActivity extends AppCompatActivity {

    static List<IParseIntent> parseIntents = new ArrayList<>();
    public static  void addParser(IParseIntent parseIntent){
        parseIntents.add(parseIntent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("获取到intent",getIntent());
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setBackground(new ColorDrawable(Color.WHITE));
        textView.setPadding(60,60,60,60);
        textView.setText(msg());
        textView.setTextIsSelectable(true);
        textView.setTextColor(Color.BLACK);
        scrollView.addView(textView);

        setContentView(scrollView);
        for (IParseIntent parseIntent : parseIntents) {
            boolean b = parseIntent.parseIntent(getIntent(), this);
            if(b){
                break;
            }
        }


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
