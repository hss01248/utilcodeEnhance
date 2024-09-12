package com.hss01248.crash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * by hss
 * data:2020-04-28
 * desc:
 */
public class TextFileDisplayActivity extends Activity {


    String path;
    String text;
    private TextView tvTitle;
    private TextView tvShare;
    private TextView tvList;
    private TextView tvText;

    public static void launch(final Context context, final String path) {
        Intent intent = new Intent(context, TextFileDisplayActivity.class);
        intent.putExtra("path", path);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.log_crash_activity);
            initView();
            path = getIntent().getStringExtra("path");
            init();
            tvList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ListFileActivity.launchCrashFiles();
                }
            });
            //tvTitle.setText(path);
            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "crash 信息:" + text);
                    intent.setType("text/plain");
                    startActivity(intent);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void init() {
        try {
            text = readText();
            String text1 = "file path : "+path+"\n\n"+text;
            tvText.setText(text1);
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* float zoomScale = 0.5f;// 缩放比例
        new ZoomTextView(textView, zoomScale);*/

    }

    public String readText() throws Exception {
        InputStream is = new FileInputStream(path);

        int index = is.available();
        byte data[] = new byte[index];
        is.read(data);
        return new String(data, "UTF-8");
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvShare = (TextView) findViewById(R.id.tv_share);
        tvList = (TextView) findViewById(R.id.tv_list);
        tvText = (TextView) findViewById(R.id.tv_text);
    }
}
