package com.hss01248.usb;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;

import com.blankj.utilcode.util.Utils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 2:25 PM
 * @Version 1.0
 */
public class UsbUtil {

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


}
