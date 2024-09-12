package com.hss.downloader.callback;

import com.hss.downloader.IDownloadCallback;
import com.hss.utils.enhance.foregroundservice.CommonProgressService;

import java.util.Random;

/**
 * @Despciption todo
 * @Author hss
 * @Date 9/9/24 4:37 PM
 * @Version 1.0
 */
public class DownloadCallbackWithServiceNotification implements IDownloadCallback {
    String path;
    int random;

    public DownloadCallbackWithServiceNotification(IDownloadCallback callback) {
        this.callback = callback;
    }

    IDownloadCallback callback;
    @Override
    public void onStart(String url, String realPath) {
        IDownloadCallback.super.onStart(url, realPath);
        callback.onStart(url, realPath);
        String path = realPath;
        if(path ==null){
            path = url;
            if(path.contains("?")){
                path = path.substring(0,path.indexOf("?"));
            }
        }
        if(path.contains("/")){
            path = path.substring(path.lastIndexOf("/")+1);
        }
         random = new Random().nextInt(900);

        CommonProgressService.startS("download " + path, "下载msg", random, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onProgress(String url, String realPath, long currentOffset, long totalLength, long speed) {
        IDownloadCallback.super.onProgress(url, realPath, currentOffset, totalLength, speed);
        callback.onProgress(url, realPath, currentOffset, totalLength, speed);
        CommonProgressService.updateProgress((int)currentOffset,(int)totalLength,path,"msg",random);
    }

    @Override
    public void onSuccess(String url, String realPath) {
        callback.onStart(url, realPath);

    }

    @Override
    public void onFail(String url, String realPath, String msg, Throwable throwable) {
        callback.onFail(url, realPath, msg, throwable);
    }

    @Override
    public void onBefore(String url, String realPath, boolean forceRedownload) {
        IDownloadCallback.super.onBefore(url, realPath, forceRedownload);
        callback.onBefore(url, realPath, forceRedownload);
    }

}
