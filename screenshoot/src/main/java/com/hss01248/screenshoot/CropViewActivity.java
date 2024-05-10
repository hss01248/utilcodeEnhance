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
import com.hss01248.screenshoot.databinding.LayoutCropViewBinding;
import com.hss01248.screenshoot.system.SystemScreenShotUtil;

import org.devio.takephoto.wrap.TakeOnePhotoListener;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/7/24 6:20 PM
 * @Version 1.0
 */
public class CropViewActivity extends AppCompatActivity {


    LayoutCropViewBinding binding;
    String path;
    int widthView, heightView;
    Rect cropRect;
    public  static void start(){
        ActivityUtils.getTopActivity().startActivityForResult(new Intent(ActivityUtils.getTopActivity(),CropViewActivity.class),158);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutCropViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemScreenShotUtil.clearRect();
                ToastUtils.showShort("裁剪区域配置已经清空");
            }
        });
        binding.btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImgDataSeletor.startPickOneWitchDialog(CropViewActivity.this, new TakeOnePhotoListener() {
                    @Override
                    public void onSuccess(String path) {
                        CropViewActivity.this.path = path;
                        ImageLoader.with(CropViewActivity.this)
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

                                        //图片原始大小:
                                        int[] imageWidthHeight = MyUtil.getImageWidthHeight(path);
                                        LogUtils.d("原始图片大小: ",imageWidthHeight[0],imageWidthHeight[1]);

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
                Dialog dialog = new Dialog(CropViewActivity.this);


                DialogDisplayBitmapBinding bitmapBinding = DialogDisplayBitmapBinding.inflate(CropViewActivity.this.getLayoutInflater());
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
                        SystemScreenShotUtil.saveRect(cropRect);
                        dialog.dismiss();
                        ToastUtils.showShort("裁剪区域配置已经保存");

                    }
                });
            }
        });
    }


}
