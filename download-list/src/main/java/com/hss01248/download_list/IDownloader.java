package com.hss01248.download_list;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hss.utils.enhance.api.MyCommonCallback;

import java.io.File;
import java.util.Map;

public interface IDownloader {


   default void download(@NonNull String url, @NonNull final MyCommonCallback<File> callback){
       String name = URLUtil.guessFileName(url,"","");

   }

    void download(@NonNull String url, @Nullable String saveDirPath, @Nullable String fileName, @Nullable Map<String,String> headers,
                  @NonNull final MyCommonCallback<File> callback);
}
