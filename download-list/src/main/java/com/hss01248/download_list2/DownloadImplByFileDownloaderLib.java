package com.hss01248.download_list2;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.hss.downloader.download.DownloadInfoUtil;
import com.hss01248.http.response.DownloadParser;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.Map;
import java.util.Set;

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

        fileName = DownloadInfoUtil.getLeagalFileName(saveDirPath, fileName);
        File file = new File(saveDirPath,fileName);
        BaseDownloadTask task =  FileDownloader.getImpl().create(url)
                .setPath(file.getAbsolutePath());
       if(headers != null){
           Set<String> strings = headers.keySet();
           for (String string : strings) {
               String val = headers.get(string);
               if(val != null){
                   task.addHeader(string,val);
               }
           }
       }
        task.addHeader(
                "User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
                //1M以上,需要wifi才下载
        task.setWifiRequired(false)
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
                        LogUtils.w(task.getUrl(),e,file.getAbsolutePath());
                        callback.onError(e.getMessage());
                    }
                })
                .start();
    }
}
