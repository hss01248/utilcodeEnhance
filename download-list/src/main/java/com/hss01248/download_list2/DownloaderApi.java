package com.hss01248.download_list2;

import android.os.Environment;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface DownloaderApi {


   default void download(@NonNull String url, @NonNull final DownloadCallback callback){
       String name = URLUtil.guessFileName(url,"","");
       download(url, mkDefaultDownloadDir(),name,new HashMap<>(),callback);

   }
     static File mkDir(File dir, boolean isPublic) {
        String pk = Utils.getApp().getPackageName();
        String fileName = pk.substring(pk.lastIndexOf(".") + 1);
        File dir2 = new File(dir, fileName);
        if (dir2.exists() && dir2.isDirectory()) {
            return dir2;
        }
        if (dir2.exists() && dir2.isFile()) {
            dir2.delete();
        }
        try {
            boolean success = dir2.mkdirs();
            if (success) {
                return dir2;
            } else {
                if (isPublic) {
                    return null;
                }
                return dir;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isPublic) {
                return null;
            }
            return dir;
        }
    }

     static String mkDefaultDownloadDir() {
        //有权限,就放到外面

        File dir = mkDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), true);
        if (dir != null) {
            return dir.getAbsolutePath();
        }


        File dir2 = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (dir2 != null) {
            return dir2.getAbsolutePath();
        }


        return Utils.getApp().getFilesDir().getAbsolutePath();
    }

    void download(@NonNull String url, @Nullable String saveDirPath, @Nullable String fileName, @Nullable Map<String,String> headers,
                  @NonNull final DownloadCallback callback);
}
