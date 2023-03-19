package com.hss01248.qrscan;

import android.Manifest;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss.utils.enhance.lifecycle.LifecycleObjectUtil;
import com.hss.utils.enhance.viewholder.MyViewHolder;
import com.hss01248.media.pick.MediaPickUtil;
import com.hss01248.permission.MyPermissions;
import com.hss01248.qrscan.databinding.ViewholderScanCodeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.BarcodeFormat;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/12/2022 15:59
 * @Version 1.0
 */
public class ScanCodeViewHolder extends MyViewHolder<ViewholderScanCodeBinding, Map<String,Object>> implements QRCodeView.Delegate {

    boolean isFlashOn = false;
    boolean hasCameraPermission = false;

    public void setScanCodeSuccessListener(ScanCodeSuccessListener scanCodeSuccessListener) {
        this.scanCodeSuccessListener = scanCodeSuccessListener;
    }

    ScanCodeSuccessListener scanCodeSuccessListener;

    public ScanCodeViewHolder(ViewGroup parent) {
        super(parent);
        initView();
        requestPermission();
    }

    private void requestPermission() {
        MyPermissions.requestByMostEffort(false, true, new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(@NonNull List<String> granted) {
               onPermissionGranted();
            }

            @Override
            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                LifecycleObjectUtil.getActivityFromContext(binding.getRoot().getContext()).finish();
            }
        }, Manifest.permission.CAMERA);
    }

    private void onPermissionGranted() {
        hasCameraPermission =true;
        initScan();
        startScan();
        BarUtils.transparentStatusBar(LifecycleObjectUtil.getActivityFromContext(binding.getRoot().getContext()));
        BarUtils.setStatusBarLightMode(LifecycleObjectUtil.getActivityFromContext(binding.getRoot().getContext()),false);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.qrTitlebar.getLayoutParams();
        layoutParams.topMargin = BarUtils.getStatusBarHeight();
        binding.qrTitlebar.setLayoutParams(layoutParams);
        // BarUtils.setStatusBarColor(LifecycleObjectUtil.getActivityFromContext(binding.getRoot().getContext()),Color.TRANSPARENT);
    }

    private void startScan() {
        if (hasCameraPermission) {
            binding.zvScan.showScanRect();
            binding.zvScan.startSpot();
        }
    }
    private void stopScan() {
        if (hasCameraPermission) {
            binding.zvScan.stopCamera();
        }
    }

    private void initScan() {
        binding.zvScan.setDelegate(this);
        List<BarcodeFormat> formatList = new ArrayList<>();
        // 同时支持二维码和条形码的扫描
        formatList.add(BarcodeFormat.QRCODE);
        formatList.add(BarcodeFormat.CODE128);
        binding.zvScan.setType(BarcodeType.CUSTOM, formatList);

        //       scanBoxView.setTopOffset(topOffset)


    }


    private void initView() {
        binding.qrTitlebar.getTitleView().setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.qrTitlebar.getLeftIcon().setTintList(ColorStateList.valueOf(Color.WHITE));
        }
        binding.qrTitlebar.getLineView().setVisibility(View.GONE);
        binding.qrTitlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                LifecycleObjectUtil.getActivityFromContext(binding.getRoot().getContext()).finish();
            }

            @Override
            public void onRightClick(TitleBar titleBar) {
                OnTitleBarListener.super.onRightClick(titleBar);
                pickFromAlbumAndParse();
            }
        });
        binding.ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    binding.zvScan.closeFlashlight();
                }else {
                    binding.zvScan.openFlashlight();
                }
                isFlashOn = !isFlashOn;
                if(isFlashOn){
                    binding.ivFlash.setImageResource(R.drawable.icon_flash_open);
                }else {
                    binding.ivFlash.setImageResource(R.drawable.icon_flash_closure);
                }
            }
        });
    }

    private void pickFromAlbumAndParse() {

        MediaPickUtil.pickImage(new MyCommonCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Bitmap bitmap = BitmapParser.parseFromUri(uri);
                    if(bitmap != null){
                        binding.zvScan.decodeQRCode(bitmap);
                    }else {
                        ToastUtils.showLong(R.string.qr_not_recognised);
                    }
                    startScan();
                } catch (Throwable e) {
                    ToastUtils.showLong(R.string.qr_not_recognised+"\n"+e.getMessage());
                    LogUtils.w(e);
                    startScan();
                }
            }
        });
    }

    @Override
    protected void assignDataAndEventReal(Map<String,Object> data) {


    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        super.onDestroy(owner);
    }


    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        super.onStop(owner);
        stopScan();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        super.onStart(owner);
       startScan();
    }

    private void  vibrate() {
        Vibrator vibrator = (Vibrator) Utils.getApp().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        ClipboardUtils.copyText(StringUtils.getString(R.string.qr_from_code_scan),result);
        if(this.scanCodeSuccessListener != null){
            scanCodeSuccessListener.onScanQRCodeSuccess(result);
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    public interface  ScanCodeSuccessListener{
        void onScanQRCodeSuccess(String result);
    }
}
