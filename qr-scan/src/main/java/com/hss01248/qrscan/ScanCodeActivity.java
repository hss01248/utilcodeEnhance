package com.hss01248.qrscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.activityresult.TheActivityListener;

import java.util.HashMap;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/12/2022 15:49
 * @Version 1.0
 */
public class ScanCodeActivity extends AppCompatActivity {

    public static void scanForResult(Consumer<String> success){
        StartActivityUtil.startActivity(ActivityUtils.getTopActivity(),
                ScanCodeActivity.class,
                null,true,
                new TheActivityListener<ScanCodeActivity>(){
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                        super.onActivityResult(requestCode, resultCode, data);
                        if(resultCode == RESULT_OK){
                            if(data != null && !TextUtils.isEmpty(data.getStringExtra("result"))){
                                try {
                                    success.accept(data.getStringExtra("result"));
                                } catch (Exception e) {
                                    LogUtils.w(e);
                                }
                            }
                        }
                    }
                });
    }

    ScanCodeViewHolder viewHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         viewHolder = new ScanCodeViewHolder(findViewById(android.R.id.content));
        viewHolder.assignDataAndEventReal(new HashMap<>());
         viewHolder.setScanCodeSuccessListener(new ScanCodeViewHolder.ScanCodeSuccessListener() {
             @Override
             public void onScanQRCodeSuccess(String result) {
                 Intent intent  = new Intent();
                 intent.putExtra("result",result);
                 setResult(Activity.RESULT_OK,intent);
                 finish();
             }
         });
        setContentView(viewHolder.getRootView());
    }
}
