package com.hss01248.screenshoot;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss01248.image.ImageLoader;
import com.hss01248.image.MyUtil;
import com.hss01248.image.dataforphotoselet.ImgDataSeletor;
import com.hss01248.image.interfaces.ImageListener;
import com.hss01248.screenshoot.databinding.LayoutCropViewBinding;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutCropViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                int[] originalWidthHeight = MyUtil.getImageWidthHeight(path);
                Bitmap bitmap = cropBitmap(bmp, cropRect, originalWidthHeight[0], originalWidthHeight[1], widthView, heightView);
                Dialog dialog = new Dialog(CropViewActivity.this);
                ImageView imageView =  new ImageView(CropViewActivity.this);
                imageView.setImageBitmap(bitmap);
                dialog.setContentView(imageView);

                dialog.show();
            }
        });
    }


    /**
     * 裁剪Bitmap
     *
     * @param source 原始的Bitmap
     * @param scaledRect 裁剪框在缩放后的图片上的位置
     * @param originalWidth 原始Bitmap的宽度
     * @param originalHeight 原始Bitmap的高度
     * @param displayWidth 显示的宽度
     * @param displayHeight 显示的高度
     * @return 裁剪后的Bitmap
     */
    public static Bitmap cropBitmap(Bitmap source, Rect scaledRect,
                                    int originalWidth, int originalHeight,
                                    int displayWidth, int displayHeight) {
        float widthRatio = (float) originalWidth / displayWidth;
        float heightRatio = (float) originalHeight / displayHeight;

        int left = Math.round(scaledRect.left * widthRatio);
        int top = Math.round(scaledRect.top * heightRatio);
        int right = Math.round(scaledRect.right * widthRatio);
        int bottom = Math.round(scaledRect.bottom * heightRatio);

        int width = right - left;
        int height = bottom - top;

        return Bitmap.createBitmap(source, left, top, width, height);
    }
}
