package com.hss01248.download_list;

import com.hss.utils.enhance.api.MyCommonCallback;

import java.io.File;

public interface DownloadCallback extends MyCommonCallback<File> {

    void onStart(String url);

    void onProgress(float progress, long total);
}
