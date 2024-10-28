package com.hss01248.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss01248.usb.util.SmartToastUtils;
import com.hss01248.viewholder_media.CommonFileTreeViewHolder;

import java.io.IOException;
import java.util.List;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.partition.Partition;

public class USBMTPReceiver extends BroadcastReceiver {

    private Context mContext;
    public final static String READ_USB_DEVICE_PERMISSION = "com.android.example.USB_PERMISSION";

    @Override
    public void onReceive(Context context, Intent intent) {
        SmartToastUtils.showShort(context, "onReceiver start");
        mContext = context;
        final String action = intent.getAction();
        switch (action) {
            case UsbManager.ACTION_USB_DEVICE_ATTACHED://插上USB设备
                SmartToastUtils.showShort(context, "USB设备已连接");
                UsbDevice find_USB_Device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                //设备不为空
                if (find_USB_Device != null) {
                    // 检查权限
                    permissionRequest(mContext);
                } else {
                    SmartToastUtils.showShort(mContext, "findUsb is null");
                }
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED://断开USB设备
                SmartToastUtils.showShort(context, "USB设备已断开");
                try {
                    UsbDevice removedDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (removedDevice != null) {
                        UsbMassStorageDevice usbMassStorageDevice = getUsbMass(removedDevice);
                        if (usbMassStorageDevice != null) {
                            usbMassStorageDevice.close();
                        }
                    }
                } catch (Exception e) {
                    SmartToastUtils.showShort(mContext, "USB断开异常" + e.toString());
                }
                break;
            case READ_USB_DEVICE_PERMISSION: //自定义读取USB 设备信息
                SmartToastUtils.showShort(context, "USB已获取权限");
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                // 检查U盘权限
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (usbDevice != null) {
                        //读取USB 设备
                        readDeviceAsync(getUsbMass(usbDevice));
                    } else {
                        SmartToastUtils.showShort(mContext, "没有插入U 盘");
                    }
                } else {
                    SmartToastUtils.showShort(mContext, "未获取到 U盘权限");
                    permissionRequest(mContext);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 进行 U 盘读写权限的申请
     */
    private void permissionRequest(Context context) {
        SmartToastUtils.showShort(context, "开始申请设备权限");
        try {
            // 设备管理器
            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            // 获取 U 盘存储设备
            UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(context.getApplicationContext());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                    0, new Intent(READ_USB_DEVICE_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            if (storageDevices.length == 0) {
                SmartToastUtils.showShort(context, "请插入可用的 U 盘");
            } else {
                //可能有几个 一般只有一个 因为大部分手机只有1个otg插口
                wait = 0;
                for (UsbMassStorageDevice device : storageDevices) {
                    if (usbManager.hasPermission(device.getUsbDevice())) {
                        SmartToastUtils.showShort(context, "USB直接获取权限");
                        readDeviceAsync(device);
                    } else {
                        // 进行权限申请
                        usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
                        //todo 使用请求框架
                        checkPermissions(usbManager, device);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.w(e);
            SmartToastUtils.showShort(context, "申请权限异常,e=" + e.toString());
        }

    }

    int wait = 0;

    private void checkPermissions(UsbManager usbManager, UsbMassStorageDevice device) {
        if (usbManager.hasPermission(device.getUsbDevice())) {
            readDeviceAsync(device);
        } else {
            if (wait > 10) {
                return;
            }
            ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    wait++;
                    checkPermissions(usbManager, device);
                }
            }, 1000);
        }
    }

    private void readDeviceAsync(UsbMassStorageDevice device) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                readDevice(device);
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });

    }

    private void readDevice(UsbMassStorageDevice device) {
        SmartToastUtils.showShort(mContext, "开始读取设备信息");
        try {
            device.init();
        } catch (IOException e) {
            SmartToastUtils.showShort(mContext, "device.init() error" + e.toString());
            return;
        }


        List<Partition> partitions = device.getPartitions();
        if (partitions == null) {
            SmartToastUtils.showShort(mContext, "partitions ==null");
            return;
        }
        if (partitions.isEmpty()) {
            //Then
            SmartToastUtils.showLong(mContext, "partitions.isEmpty()," +
                    "\nyou are either using an unsupported partition table (e.g. GPT instead of MBR) or an unsupported file system. " +
                    "Only FAT32 is supported.\n" +
                    "maybe you are using a exFAT instead of FAT32. Just formatted it to FAT32 and problem was gone.\n" +
                    "There is a module in the repo java-fs which includes exFAT and NTFS (read-only)");
            //GPT now supported!  2023

            return;
        }
        LogUtils.d("partitions", partitions);
        // 设备分区
        Partition partition = device.getPartitions().get(0);
        // 文件系统
        FileSystem currentFs = partition.getFileSystem();
        // 获取 U 盘的根目录
        UsbFile mRootFolder = currentFs.getRootDirectory();

        ThreadUtils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                CommonFileTreeViewHolder.viewDirInActivity(new UsbFileImpl(mRootFolder));
            }
        });

//        // 获取 U 盘的容量
//        long capacity = currentFs.getCapacity();
//        // 获取 U 盘的剩余容量
//        long freeSpace = currentFs.getFreeSpace();
//        // 获取 U 盘的标识
//        String volumeLabel = currentFs.getVolumeLabel();

//        if(mRootFolder.isDirectory()){
//            SmartToastUtils.showShort(mContext,"这是一个文件夹");
//        }else{
//            SmartToastUtils.showShort(mContext,"这不是一个文件夹");
//        }
//        if(mRootFolder.isRoot()){
//            SmartToastUtils.showShort(mContext,"这是根目录");
//        }else{
//            SmartToastUtils.showShort(mContext,"这不是根目录");
//        }
        readAllPicFileFromUSBDevice(mRootFolder, currentFs);
    }

    private void readAllPicFileFromUSBDevice(UsbFile usbFile, FileSystem fileSystem) {
        try {
            UsbFile[] usbFileList = usbFile.listFiles();
            for (UsbFile usbFileItem : usbFileList) {
                LogUtils.d(usbFileItem.getName(), usbFileItem.getAbsolutePath(),
                        "isDirectory" + usbFileItem.isDirectory(), usbFileItem.isDirectory() ? 0 : usbFileItem.getLength());
                if (!usbFileItem.isDirectory()) {
                    String file = usbFileItem.getName().substring(usbFileItem.getName().lastIndexOf(".") + 1,
                            usbFileItem.getName().length()).toLowerCase();
                    if (file.equals("jpg") || file.equals("png") || file.equals("gif")
                            || file.equals("jpeg") || file.equals("bmp")) {
                        LogUtils.i("找到了图片",usbFileItem.getName());
                        //SmartToastUtils.showShort(mContext,"文件名称="+usbFileItem.getName()+"文件大小="+usbFileItem.getLength());
                        //FileUtils.saveToPhoneDevice(usbFileItem,fileSystem);
                    }
                } else {
                    readAllPicFileFromUSBDevice(usbFileItem, fileSystem);
                }
            }
        } catch (IOException e) {
            SmartToastUtils.showShort(mContext, "遍历USB文件异常");
        }
    }

    /**
     * USBDevice 转换成UsbMassStorageDevice 对象
     */
    private UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(mContext);
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }
}
