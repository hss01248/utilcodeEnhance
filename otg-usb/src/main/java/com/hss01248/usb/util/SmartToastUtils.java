package com.hss01248.usb.util;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.toast.MyToast;

/**
 * Toast 弹出信息工具类,简化代码编写
 * @author fairy
 * */
public class SmartToastUtils{
    public static void showLong(Context context, String info) {
        MyToast.show(info);
        LogUtils.w(info);
    }
    public static void showShort(Context context,String info) {
        MyToast.show(info);
        LogUtils.d(info);

    }
}
