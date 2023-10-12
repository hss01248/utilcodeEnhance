package com.hss01248.basewebview;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.MyKeyboardUtil;


@Keep
public class BaseWebviewActivity extends AppCompatActivity implements ISetWebviewHolder{


    public static void start(Activity activity, String url){
        Intent intent = new Intent(activity, BaseWebviewActivity.class);
        intent.putExtra("url",url);
        activity.startActivity(intent);
    }

    public static void start( Activity activity,String url,String title){
        Intent intent = new Intent(activity, BaseWebviewActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        activity.startActivity(intent);
    }

    protected String url  = "";
    protected String title  = "";
   protected BaseQuickWebview quickWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        if(!getIntent().getBooleanExtra(ISetWebviewHolder.setWebviewHolderByOutSide,false)){
            setContentView(R.layout.default_webview_container);
           quickWebview = findViewById(R.id.root_ll);

            initWebview2(quickWebview);
            quickWebview.setTitleFromIntent(title);
           quickWebview.loadUrl(url);

        }
    }

    protected  void initWebview2(BaseQuickWebview quickWebview) {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.w(newConfig);
    }

    @Override
    public void setWebviewHolder(BaseQuickWebview webview) {
        this.quickWebview = webview;

    }

    @Override
    public void onBackPressed() {
        if(quickWebview == null || !quickWebview.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
