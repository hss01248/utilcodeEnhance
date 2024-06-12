package com.hss01248.basewebview.download;

import com.hss.downloader.api.DownloadApi;
import com.hss.downloader.callback.DefaultSilentDownloadCallback;
import com.hss01248.basewebview.IDownloader;

/**
 * @Despciption todo
 * @Author hss
 * @Date 6/12/24 5:34 PM
 * @Version 1.0
 */
public class ApiDownloader implements IDownloader {
    @Override
    public void doDownload(String url, String name, String dir) {
        DownloadApi.create(url)
                .callback(new DefaultSilentDownloadCallback());
    }
}
