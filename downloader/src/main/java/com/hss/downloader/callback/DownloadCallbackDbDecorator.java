package com.hss.downloader.callback;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.downloader.IDownloadCallback;
import com.hss.downloader.api.DownloadApi;
import com.hss.downloader.download.DownloadInfo;
import com.hss.downloader.download.DownloadInfoUtil;
import com.hss01248.img.compressor.ImageCompressor;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadCallbackDbDecorator implements IDownloadCallback {

    public DownloadCallbackDbDecorator(IDownloadCallback callback) {
        this.callback = callback;
    }

    IDownloadCallback callback;

    public DownloadCallbackDbDecorator setApi(DownloadApi api) {
        this.api = api;
        return this;
    }

    DownloadApi api;

    @Override
    public void onBefore(String url, String realPath, boolean forceRedownload) {
        callback.onBefore(url, realPath, forceRedownload);
    }


    public static File shouldStartRealDownload(String url, String realPath, boolean forceRedownload, DownloadApi api) {
        DownloadInfo load = DownloadInfoUtil.getDao().load(url);
        if (load != null) {
            load.updateTime = System.currentTimeMillis();
            if (load.downloadSuccess() && !forceRedownload && load.filePath !=null) {
                File file = new File(load.filePath);
                if (file.exists() && file.length() > 0) {
                    load.status = DownloadInfo.STATUS_SUCCESS;
                    DownloadInfoUtil.getDao().update(load);
                    LogUtils.w("已经下载成功", load.filePath,load.url);
                    //callback.onSuccess(url,load.filePath);
                    return file;
                    //return ;
                } else {
                    LogUtils.w("下载成功过,但文件不存在,比如剪切到其他地方了", load);
                }
            } else {
                DownloadInfoUtil.getDao().update(load);
            }
        } else {
            load = new DownloadInfo();
            load.createTime = System.currentTimeMillis();
            load.updateTime = System.currentTimeMillis();
            load.status = DownloadInfo.STATUS_ORIGINAL;
            load.url = url;
            load.resetFilePath(realPath);
            DownloadInfoUtil.getDao().insert(load);
        }
        return null;
    }

    private void exeOnIo(Runnable runnable) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                runnable.run();
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }

    @Override
    public void onStart(String url, String realPath) {
        callback.onStart(url, realPath);
        exeOnIo(new Runnable() {

            @Override
            public void run() {
                DownloadInfo load = DownloadInfoUtil.getDao().load(url);
                if (load == null) {
                    LogUtils.w("download info in db == null , " + url);
                    return;
                }
                load.status = DownloadInfo.STATUS_DOWNLOADING;
                DownloadInfoUtil.getDao().update(load);
                EventBus.getDefault().post(load);
            }
        });

    }

    @Override
    public void onSuccess(String url, String realPath) {

        exeOnIo(new Runnable() {
            @Override
            public void run() {
                String path2 = realPath;
                //压缩图片
                File compress = ImageCompressor.compress(realPath, false, false);
                if (!compress.exists() || compress.length() == 0) {
                    LogUtils.e("file not exist after compress");
                    onFail(url, compress.getAbsolutePath(), "compress failed,file not exist", new Throwable("xxx"));
                    return;
                }
                path2 = compress.getAbsolutePath();
                long len = compress.length();
                //裁剪到mediastore
                if (api.isCutToMediaStore()) {
                    boolean success = cutFileToMediaStore(compress,
                            api.getMediaStoreRelativePath() +"/"+ compress.getName());
                    if (success) {
                        path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                                + api.getMediaStoreRelativePath()+"/"+ compress.getName();
                    }
                }
                callback.onSuccess(url, path2);
                DownloadInfo load = DownloadInfoUtil.getDao().load(url);
                if (load == null) {
                    LogUtils.e("download info in db == null onSuccess, " + url);
                } else {
                    load.status = DownloadInfo.STATUS_SUCCESS;
                    load.resetFilePath(path2);
                    load.totalLength = len;
                    load.currentOffset = len;
                    DownloadInfoUtil.getDao().update(load);
                    EventBus.getDefault().post(load);
                }
            }
        });

    }

    long lastTime = 0;

    @Override
    public void onProgress(String url, String realPath, long currentOffset, long totalLength, long speed) {
        if (currentOffset == totalLength) {
            lastTime = 0;
        }
        if (System.currentTimeMillis() - lastTime < 300) {
            //每秒更新一次进度
            return;
        }
        lastTime = System.currentTimeMillis();
        exeOnIo(new Runnable() {
            @Override
            public void run() {
                DownloadInfo load = DownloadInfoUtil.getDao().load(url);
                if (load == null) {
                    LogUtils.w("download info in db == null , " + url);
                    return;
                }
                load.status = DownloadInfo.STATUS_DOWNLOADING;
                load.currentOffset = currentOffset;
                load.totalLength = totalLength;
                DownloadInfoUtil.getDao().update(load);
                EventBus.getDefault().post(load);
            }
        });
        callback.onProgress(url, realPath, currentOffset, totalLength, speed);
    }

    @Override
    public void onFail(String url, String realPath, String msg, Throwable throwable) {


        exeOnIo(new Runnable() {
            @Override
            public void run() {
                DownloadInfo load = DownloadInfoUtil.getDao().load(url);
                if (load == null) {
                    LogUtils.w("download info in db == null , " + url);
                    return;
                }
                load.status = DownloadInfo.STATUS_FAIL;
                load.errMsg = msg;
                DownloadInfoUtil.getDao().update(load);
                EventBus.getDefault().post(load);
            }
        });
        callback.onFail(url, realPath, msg, throwable);

    }

    public static boolean cutFileToMediaStore(File file, String realPath) {
        //MediaStore.Files
        Uri uri = MediaStore.Files.getContentUri("external");
        ContentValues contentValues = new ContentValues();
        //contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/"+AppUtils.getAppName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, realPath);
        } else {
            contentValues.put(
                    MediaStore.MediaColumns.DATA,
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + realPath
            );
        }
            // 设置文件名称
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, file.getName());
            // 设置文件标题, 一般是删除后缀, 可以不设置
            //contentValues.put(MediaStore.Downloads.TITLE, "hello");
            // uri 表示操作哪个数据库 , contentValues 表示要插入的数据内容
            Uri insert = Utils.getApp().getContentResolver().insert(uri, contentValues);
            // 向 Download/hello/hello.txt 文件中插入数据

            try {
                int sBufferSize = 524288;
                InputStream is = new FileInputStream(file);
                OutputStream os = Utils.getApp().getContentResolver().openOutputStream(insert);
                try {
                    os = new BufferedOutputStream(os, sBufferSize);

                    double totalSize = is.available();
                    int curSize = 0;

                    byte[] data = new byte[sBufferSize];
                    for (int len; (len = is.read(data)) != -1; ) {
                        os.write(data, 0, len);
                        curSize += len;
                    }
                    os.flush();
                    LogUtils.i("cut文件到文件夹: " + realPath);
                    //ToastUtils.showLong("文件下载到download文件夹: download/" + AppUtils.getAppName() + "/" + file.getName());
                    file.delete();
                    return true;

                } catch (Throwable e) {
                    LogUtils.w("cut文件到mediastore失败1:", e);
                    return false;

                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LogUtils.w(e);
                    }
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (IOException e) {
                        LogUtils.w(e);
                    }
                }
            } catch (Exception e) {
                LogUtils.w("cut文件到mediastore失败2:", e);
                return false;
            }
        }
    }


