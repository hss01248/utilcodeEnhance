package com.hss01248.openuri2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class OpenUri2 {


    public static Uri fromFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider2", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Log.d("uri", uri.toString());
        return uri;
    }

    public static void addPermissionR(Intent intent) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    public static void addPermissionRW(Intent intent) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }
}
