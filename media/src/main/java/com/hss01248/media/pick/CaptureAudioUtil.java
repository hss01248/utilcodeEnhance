package com.hss01248.media.pick;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.activityresult.ActivityResultListener;
import com.hss01248.activityresult.StartActivityUtil;
import com.hss01248.permission.MyPermissions;

import java.util.List;

public class CaptureAudioUtil {

    public static void startRecord(MyCommonCallback<Uri> callback){
        MyPermissions.requestByMostEffort(false, true,
                new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

                        /*File externalFilesDir = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                        if(externalFilesDir == null){
                            externalFilesDir = new File(Utils.getApp().getFilesDir(),Environment.DIRECTORY_MOVIES);
                        }
                        externalFilesDir.mkdirs();
                        //Android录音支持的格式有amr、aac，但这两种音频格式在跨平台上表现并不好。
                        // MP3显然才是跨平台的最佳选择。 实现MP3格式最好是借助Lame这个成熟的解决方案
                        File file = new File(externalFilesDir,System.currentTimeMillis()+".amr");
                        Uri uri= OpenUri.fromFile(Utils.getApp(),file);
                        OpenUri.addPermissionRW(intent);
                        // 设置系统相机拍摄照片完成后图片文件的存放地址
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);*/

                        PackageManager manager = Utils.getApp().getPackageManager();
                        if (manager.queryIntentActivities(intent, 0).size() <= 0) {
                            MediaPickUtil.pickAudio(callback);
                            return;
                        }


                        StartActivityUtil.goOutAppForResult(ActivityUtils.getTopActivity(), intent,
                                new ActivityResultListener() {
                                    @Override
                                    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                                        if(resultCode == Activity.RESULT_CANCELED){
                                            callback.onError("canceled");
                                            return;
                                        }
                                        if(resultCode != Activity.RESULT_OK){
                                            LogUtils.i("result code is not RESULT_OK:"+resultCode);
                                        }
                                        LogUtils.i(data);
                                        if(data == null || data.getData() == null){
                                            callback.onError("return data is null");
                                            return;
                                        }
                                        callback.onSuccess(data.getData());
                                        /*if(file.length()>0){
                                            callback.onSuccess(Uri.fromFile(file));
                                        }else {
                                             if(data == null || data.getData() == null){
                                                callback.onError("return data is null");
                                                return;
                                            }
                                            callback.onSuccess(data.getData());
                                        }*/
                                    }

                                    @Override
                                    public void onActivityNotFound(Throwable e) {
                                        callback.onError(e.getClass().getSimpleName(),e.getMessage(),e);
                                    }
                                });
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        callback.onError("RECORD_AUDIO permission denied");
                    }
                }, Manifest.permission.RECORD_AUDIO);

    }
}
