package com.hss01248.download_list;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.http.response.DownloadParser;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 13/01/2023 15:27
 * @Version 1.0
 */
public class DownloadImplByFileDownloaderLib implements DownloaderApi{
    @Override
    public void download(@NonNull String url, @Nullable String saveDirPath,
                         @Nullable String fileName, @Nullable Map<String, String> headers, @NonNull DownloadCallback callback) {
        if(TextUtils.isEmpty(saveDirPath)){
            saveDirPath = DownloadParser.mkDefaultDownloadDir();
        }
        if(TextUtils.isEmpty(fileName)){
            fileName = URLUtil.guessFileName(url,"","");
        }
        File file = new File(saveDirPath,fileName);
        FileDownloader.getImpl().create(url)
                .setPath(file.getAbsolutePath())
                //1M以上,需要wifi才下载
                .setWifiRequired(false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setForceReDownload(false)
                .setListener(new FileDownloadSampleListener(){
                    @Override
                    protected void started(BaseDownloadTask task) {
                        super.started(task);
                        callback.onStart(url);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        float progress = 0f;
                        if(totalBytes != 0){
                            progress = soFarBytes*1f/totalBytes;
                        }
                        callback.onProgress(progress,totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        callback.onSuccess(file);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        LogUtils.w(task.getUrl(),e);
                        callback.onError(e.getMessage());
                    }
                })
                .start();
    }
}
