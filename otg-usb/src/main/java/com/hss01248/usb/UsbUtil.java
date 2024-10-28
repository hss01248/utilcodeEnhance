package com.hss01248.usb;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.viewholder_media.api.AbsFile;
import com.hss01248.viewholder_media.scan.MediaScanCallback;
import com.hss01248.viewholder_media.scan.TreeScanUtil;

import java.io.File;
import java.util.List;

import me.jahnen.libaums.core.fs.UsbFile;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 2:25 PM
 * @Version 1.0
 */
public class UsbUtil {


    public static String usbIndentifed = "";

    /*****
     * 动态注册USB 设备监听
     * */
    public static void registerUSBReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //自定义USB设备读取照片
        intentFilter.addAction(USBMTPReceiver.READ_USB_DEVICE_PERMISSION);
        //USB连接状态发生变化时产生的广播
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        USBMTPReceiver usbmtpReceiver = new USBMTPReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Utils.getApp().registerReceiver(usbmtpReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }else {
            Utils.getApp().registerReceiver(usbmtpReceiver, intentFilter);
        }
    }


    /**
     *                                                                                                     ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
     *                                                                                                     │ args[0] = sd卡总容量
     *                                                                                                     │ args[1] = 119.083GB
     *                                                                                                     │ args[2] = 剩余容量
     *                                                                                                     │ args[3] = 119.083GB
     *                                                                                                     │ args[4] = 标识
     *                                                                                                     │ args[5] = NIKON_Z30
     *                                                                                                     │ args[6] = 根目录
     *                                                                                                     │ args[7] = /
     *                                                                                                     │ args[8] = getType
     *                                                                                                     │ args[9] = 2
     *                                                                                                     │ args[10] = 已使用容量
     *                                                                                                     │ args[11] = 44.906MB
     */
    public static void viewUsb() {
        //发送广播
        Intent intent=new Intent(USBMTPReceiver.READ_USB_DEVICE_PERMISSION);
        //发送标准广播
        Utils.getApp().sendBroadcast(intent);
    }


    public static void copyToMediaStore(UsbFile root){
        TreeScanUtil.findAllImages(new UsbFileImpl(root), true, new MediaScanCallback() {
            @Override
            public void onProgress(AbsFile file, List<AbsFile> list) {
                String usbPath = file.getAbsolutePath();
                usbPath = usbPath.substring(0,usbPath.lastIndexOf("."));
                String realPath = "/"+usbIndentifed+usbPath;
                String pathOnPhone = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() +realPath;
                //无法通过file来判断,只能通过mediastore查询:
                File file1 = new File(pathOnPhone);
                //todo
                if(file1.exists() && file1.length()>0){
                    LogUtils.d("对应文件已经存在:",file1.getAbsolutePath());
                    ToastUtils.showShort("对应文件已经存在:"+file1.getAbsolutePath());
                    return;
                }
                /*AbsMediaStoreUtil.writeMediaToMediaStore(file, realPath, file.getName(), new MyCommonCallback3<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                });*/


            }

            @Override
            public void onFinish(List<AbsFile> files) {

            }
        });
    }


}
