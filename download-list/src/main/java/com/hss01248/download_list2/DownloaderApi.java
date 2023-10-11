package com.hss01248.download_list2;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hss01248.http.response.DownloadParser;
import java.util.HashMap;
import java.util.Map;

public interface DownloaderApi {


   default void download(@NonNull String url, @NonNull final DownloadCallback callback){
       String name = URLUtil.guessFileName(url,"","");
       download(url, DownloadParser.mkDefaultDownloadDir(),name,new HashMap<>(),callback);

   }

    void download(@NonNull String url, @Nullable String saveDirPath, @Nullable String fileName, @Nullable Map<String,String> headers,
                  @NonNull final DownloadCallback callback);
}
