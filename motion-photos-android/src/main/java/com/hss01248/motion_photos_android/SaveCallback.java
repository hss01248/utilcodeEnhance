package com.hss01248.motion_photos_android;

import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.ContentUriUtil;
import com.hss01248.toast.MyToast;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/16/24 3:35 PM
 * @Version 1.0
 */
public class SaveCallback implements MyCommonCallback3<String> {
    @Override
    public void onSuccess(String s) {
        String path = s;
        if(s.startsWith("content://")){
            path =  ContentUriUtil.getRealPath(Uri.parse(s));
        }
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        if(path ==null){
            LogUtils.w("path ==null");
            return;
        }
        if(path.startsWith(root)){
            path = path.substring(root.length()+1);
        }

        MyToast.error("文件保存到: "+path);
    }

    @Override
    public void onError(String code, String msg, @Nullable Throwable throwable) {
        MyCommonCallback3.super.onError(code, msg, throwable);
        MyToast.error(msg);
    }
}
