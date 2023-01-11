package com.hss01248.basewebview;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.MyKeyboardUtil;


public class BaseWebviewActivity extends AppCompatActivity implements ISetWebviewHolder{


    public static void start(Activity activity, String url){
        Intent intent = new Intent(activity, BaseWebviewActivity.class);
        intent.putExtra("url",url);
        activity.startActivity(intent);
    }

    protected String url  = "";
   protected BaseQuickWebview quickWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        if(!getIntent().getBooleanExtra(ISetWebviewHolder.setWebviewHolderByOutSide,false)){
            setContentView(R.layout.default_webview_container);
           quickWebview = findViewById(R.id.root_ll);

            initWebview2(quickWebview);
           quickWebview.loadUrl(url);
           initKeyBoard();
        }
    }

    KeyboardUtils.OnSoftInputChangedListener inputChangedListener;
    int totalHeight = 0;
    private void initKeyBoard() {
        quickWebview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(totalHeight ==0){
                    totalHeight = quickWebview.getMeasuredHeight();
                    if(totalHeight>0){
                        quickWebview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                }
            }
        });

        //内部自动在ondestory时取消
        new MyKeyboardUtil(this)
                .addOnKeyBoardStateListener(new MyKeyboardUtil.OnKeyBoardStateListener() {
                    @Override
                    public void onSoftKeyBoardShow(int keyboardHeight) {
                        ViewGroup.LayoutParams layoutParams = quickWebview.getLayoutParams();
                        layoutParams.height = totalHeight - keyboardHeight;
                        quickWebview.setLayoutParams(layoutParams);
                    }

                    @Override
                    public void onSoftKeyBoardHide() {
                        ViewGroup.LayoutParams layoutParams = quickWebview.getLayoutParams();
                        layoutParams.height = totalHeight ;
                        quickWebview.setLayoutParams(layoutParams);
                    }
                });
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
        initKeyBoard();
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
