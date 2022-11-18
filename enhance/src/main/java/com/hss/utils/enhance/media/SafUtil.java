package com.hss.utils.enhance.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;

/**
 * @Despciption todo
 * @Author hss
 * @Date 18/11/2022 09:46
 * @Version 1.0
 */
public class SafUtil {

    //static String androidDataUri = "content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata";
    static String androidDataUri =  "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata";

    public static void askAndroidDataDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            LogUtils.i("isGrantAndroidData()",isGrantAndroidData());
            if(isGrantAndroidData()){
                parseDoc(Uri.parse(androidDataUri));
                return;
            }

            Uri uri = Uri.parse(androidDataUri);
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), intent, new ActivityResultListener() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                    if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        //检查uri是否为Android/data  Android%2Fdata
                        String url = uri.toString();
                        if(url.endsWith("%3AAndroid%2Fdata")){
                            ToastUtils.showLong("请选择Android/data目录");
                            return;
                        }
                        parseDoc(uri);
                    }
                }

                @Override
                public void onActivityNotFound(Throwable e) {

                }
            });
        }
    }

    private static void parseDoc(Uri uri) {
        MediaPickUtil.doQuery(uri);
        //将权限持久化,否则关机失效
        Utils.getApp().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


        DocumentFile documentFile = DocumentFile.fromTreeUri(Utils.getApp(), uri);
        if (documentFile == null) {
            LogUtils.w("documentFile ==null", uri);
            return;
        }
        DocumentFile[] documentFiles = documentFile.listFiles();
        if (documentFiles == null || documentFiles.length == 0) {
            LogUtils.w("documentFiles.length ==0", uri);
            return;
        }
        for (DocumentFile file : documentFiles) {
            LogUtils.d(file.getName());
        }

    }

    public static boolean isGrantAndroidData() {
        LogUtils.d("getPersistedUriPermissions",Utils.getApp().getContentResolver().getPersistedUriPermissions());
        for (UriPermission persistedUriPermission : Utils.getApp().getContentResolver().getPersistedUriPermissions()) {
            LogUtils.d(persistedUriPermission.getUri().toString());
            if (persistedUriPermission.getUri().toString().equals(androidDataUri)) {
                return true;
            }
        }
        return false;
    }
}
