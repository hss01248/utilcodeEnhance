package com.hss01248.screenshoot;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss01248.image.ImageLoader;
import com.hss01248.image.MyUtil;
import com.hss01248.image.dataforphotoselet.ImgDataSeletor;
import com.hss01248.image.interfaces.ImageListener;
import com.hss01248.screenshoot.databinding.DialogDisplayBitmapBinding;
import com.hss01248.screenshoot.databinding.LayoutCropConfigBinding;
import com.hss01248.screenshoot.system.SystemScreenShotUtil;

import org.devio.takephoto.wrap.TakeOnePhotoListener;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/7/24 6:20 PM
 * @Version 1.0
 */
public class CropConfigActivity extends AppCompatActivity {


    LayoutCropConfigBinding binding;
    String path;
    int widthView, heightView;
    Rect cropRect;
    public  static void start(boolean landscape){
        Intent intent = new Intent(ActivityUtils.getTopActivity(), CropConfigActivity.class);
        intent.putExtra("landscape",landscape);
        ActivityUtils.getTopActivity().startActivity(intent);
    }

    boolean landscape;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        landscape = getIntent().getBooleanExtra("landscape",false);
        binding = LayoutCropConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String text  = landscape? "横屏图": "竖屏图";
        binding.btnPick.setText("选择图片: "+text);


        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemScreenShotUtil.clearRect(landscape);
                ToastUtils.showShort("裁剪区域配置已经清空");
            }
        });

        binding.btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImgDataSeletor.startPickOneWitchDialog(CropConfigActivity.this, new TakeOnePhotoListener() {
                    @Override
                    public void onSuccess(String path) {
                        CropConfigActivity.this.path = path;
                        int[] imageWidthHeight = MyUtil.getImageWidthHeight(path);
                        LogUtils.d("原始图片大小: ",imageWidthHeight[0],imageWidthHeight[1]);
                        if(imageWidthHeight[0] > imageWidthHeight[1]){
                            if(!landscape){
                                ToastUtils.showShort("请选择竖屏图片(高>宽)");
                                return;
                            }
                        }

                        if(imageWidthHeight[0] < imageWidthHeight[1]){
                            if(landscape){
                                ToastUtils.showShort("请选择横屏图片(宽>高)");
                                return;
                            }
                        }
                        ImageLoader.with(CropConfigActivity.this)
                                .load(path)
                                .setImageListener(new ImageListener() {
                                    @Override
                                    public void onSuccess(@NonNull Drawable drawable, @Nullable Bitmap bitmap, int bWidth, int bHeight) {
                                        LogUtils.d(drawable,bitmap,bWidth,bHeight,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());

                                        ThreadUtils.getMainHandler().postDelayed(new Runnable(){

                                            @Override
                                            public void run() {
                                                LogUtils.d("imageview: ",binding.iv.getMeasuredWidth(),binding.iv.getMeasuredHeight());
                                                cropRect = new Rect(0,0,binding.iv.getMeasuredWidth(),binding.iv.getMeasuredHeight());
                                                binding.cropView.setOriginalRect(cropRect);
                                            widthView = binding.iv.getMeasuredWidth();
                                            heightView = binding.iv.getMeasuredHeight();
                                            }
                                        },1000);
                                    }

                                    @Override
                                    public void onFail(Throwable e) {

                                    }
                                })
                                .into(binding.iv);

                    }

                    @Override
                    public void onFail(String path, String msg) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });


        binding.cropView.setOnCropRectUpdateListener(new CustomCropView.OnCropRectUpdateListener() {
            @Override
            public void onCropRectUpdate(Rect rect) {
                cropRect = rect;
                // 处理裁剪区域变化
                Log.d("CROP", "Updated crop rect: " + rect.toString());
            }
        });
        binding.btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bmp = BitmapFactory.decodeFile(path);
                Bitmap bitmap = SystemScreenShotUtil.cropBitmap(bmp, cropRect,  widthView);
                Dialog dialog = new Dialog(CropConfigActivity.this);


                DialogDisplayBitmapBinding bitmapBinding = DialogDisplayBitmapBinding.inflate(CropConfigActivity.this.getLayoutInflater());
                bitmapBinding.dialogIv.setImageBitmap(bitmap);
                dialog.setContentView(bitmapBinding.getRoot());
                dialog.show();

                bitmapBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                bitmapBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //保存rect信息:
                        SystemScreenShotUtil.saveRect(cropRect,landscape);
                        dialog.dismiss();
                        ToastUtils.showShort("裁剪区域配置已经保存");
                        CropConfigActivity.this.finish();

                    }
                });
            }
        });
    }


}
