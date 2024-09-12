package com.hss.downloader.callback;

import android.app.ProgressDialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss.downloader.IDownloadCallback;

import java.net.URLDecoder;

/**
 * @author: Administrator
 * @date: 2023/2/4
 * @desc: //todo
 */
public class DefaultUIDownloadCallback implements IDownloadCallback {

    public DefaultUIDownloadCallback(IDownloadCallback callback) {
        this.callback = callback;
    }

    IDownloadCallback callback;
    ProgressDialog dialog;

    @Override
    public void onBefore(String url, String realPath, boolean forceRedownload) {
        callback.onBefore(url, realPath, forceRedownload);
    }

    @Override
    public void onStart(String url, String realPath) {
         dialog =  new ProgressDialog(ActivityUtils.getTopActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         String msg = "文件下载中:"+ URLDecoder.decode(url)+"\n-->\n"+realPath+"\n";
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setCancelable(false);
        dialog.show();
        callback.onStart(url, realPath);
    }

    @Override
    public void onProgress(String url, String realPath, long currentOffset, long totalLength,long speed) {
        String msg = "文件下载中:"+url+"\n-->\n"+realPath+"\n";
        msg += ConvertUtils.byte2FitMemorySize(currentOffset,1)+"/"+ConvertUtils.byte2FitMemorySize(totalLength,1);
        msg += ", "+ speed/1024+"KB/s";
        if (dialog != null){
            dialog.setMessage(msg);
            dialog.setMax((int) totalLength);
            dialog.setProgress((int) currentOffset);
            if(currentOffset == totalLength && currentOffset >0){
                dialog.dismiss();
            }

        }
        callback.onProgress(url, realPath, currentOffset, totalLength,speed);
    }

    @Override
    public void onSuccess(String url, String realPath) {
        if (dialog != null) dialog.dismiss();
        ToastUtils.showLong("文件下载成功\n"+url+"\n-->\n"+realPath);
        callback.onSuccess(url, realPath);
    }


    @Override
    public void onFail(String url, String realPath, String msg, Throwable throwable) {
        if (dialog != null)dialog.dismiss();
        ToastUtils.showLong("文件下载失败:\n"+msg+"\n"+url+"\n-->\n"+realPath);
        callback.onFail(url, realPath, msg, throwable);
    }
}
