package com.hss01248.screenshoot;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/7/24 6:20 PM
 * @Version 1.0
 */
public class CropViewActivity extends AppCompatActivity {

    public  static void start(){
        ActivityUtils.getTopActivity().startActivityForResult(new Intent(ActivityUtils.getTopActivity(),CropViewActivity.class),158);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_crop_view);

        CustomCropView cropView = findViewById(R.id.crop_view);
        cropView.setOnCropRectUpdateListener(new CustomCropView.OnCropRectUpdateListener() {
            @Override
            public void onCropRectUpdate(Rect rect) {
                // 处理裁剪区域变化
                Log.d("CROP", "Updated crop rect: " + rect.toString());
            }
        });
    }
}
