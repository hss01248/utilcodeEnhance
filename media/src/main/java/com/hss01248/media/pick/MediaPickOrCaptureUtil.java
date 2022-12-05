package com.hss01248.media.pick;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.File;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/11/2022 19:51
 * @Version 1.0
 */
public class MediaPickOrCaptureUtil {

    public static void pickImageOrTakePhoto(boolean useFrontCamera,MyCommonCallback<Uri> callback){
        new XPopup.Builder(ActivityUtils.getTopActivity())
                .asBottomList("请选择", new String[]{"拍照", "从相册选图"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if(position ==0){
                                    CaptureImageUtil.takePicture(useFrontCamera,new MyCommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            callback.onSuccess(Uri.fromFile(new File(s)));
                                        }

                                        @Override
                                        public void onError(String code, String msg, @Nullable Throwable throwable) {
                                            callback.onError(code, msg, throwable);
                                        }
                                    });
                                }else if(position ==1){
                                    MediaPickUtil.pickImage(callback);
                                }
                            }
                        })
                .show();
    }

    public static void pickOrRecordAudio(MyCommonCallback<Uri> callback){
        new XPopup.Builder(ActivityUtils.getTopActivity())
                .asBottomList("请选择", new String[]{"录音", "选择音频"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if(position ==0){
                                    CaptureAudioUtil.startRecord(new MyCommonCallback<Uri>() {
                                        @Override
                                        public void onSuccess(Uri s) {
                                            callback.onSuccess(s);
                                        }

                                        @Override
                                        public void onError(String code, String msg, @Nullable Throwable throwable) {
                                            callback.onError(code, msg, throwable);
                                        }
                                    });
                                }else if(position ==1){
                                    MediaPickUtil.pickAudio(callback);
                                }
                            }
                        })
                .show();
    }

    public static void pickOrRecordVideo(boolean useFrontCamera,int maxDurationInSecondOfVideo,MyCommonCallback<Uri> callback){
        new XPopup.Builder(ActivityUtils.getTopActivity())
                .asBottomList("请选择", new String[]{"录制视频", "从相册选择视频"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if(position ==0){
                                    CaptureVideoUtil.startVideoCapture(useFrontCamera,maxDurationInSecondOfVideo,1024*1024*1024,new MyCommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            callback.onSuccess(Uri.fromFile(new File(s)));
                                        }

                                        @Override
                                        public void onError(String code, String msg, @Nullable Throwable throwable) {
                                            callback.onError(code, msg, throwable);
                                        }
                                    });
                                }else if(position ==1){
                                    MediaPickUtil.pickVideo(callback);
                                }
                            }
                        })
                .show();
    }

    public static void pickImageOrVideo(MyCommonCallback<Uri> callback){
        new XPopup.Builder(ActivityUtils.getTopActivity())
                .asBottomList("请选择", new String[]{"图片", "视频"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if(position ==0){
                                    MediaPickUtil.pickImage(callback);
                                }else if(position ==1){
                                    MediaPickUtil.pickVideo(callback);
                                }
                            }
                        })
                .show();
    }
    public static void pickOrCaptureImageOrVideo(boolean useFrontCamera,int maxDurationInSecondOfVideo,MyCommonCallback<Uri> callback){
        new XPopup.Builder(ActivityUtils.getTopActivity())
                .asBottomList("请选择", new String[]{"从相册选择图片", "从相册选择视频","拍照","录制视频"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if(position ==0){
                                    MediaPickUtil.pickImage(callback);
                                }else if(position ==1){
                                    MediaPickUtil.pickVideo(callback);
                                }else if(position ==2){
                                    CaptureImageUtil.takePicture(useFrontCamera,new MyCommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            callback.onSuccess(Uri.fromFile(new File(s)));
                                        }

                                        @Override
                                        public void onError(String code, String msg, @Nullable Throwable throwable) {
                                            callback.onError(code, msg, throwable);
                                        }
                                    });
                                }else if(position ==4){
                                    CaptureVideoUtil.startVideoCapture(useFrontCamera,maxDurationInSecondOfVideo,1024*1024*1024,new MyCommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            callback.onSuccess(Uri.fromFile(new File(s)));
                                        }

                                        @Override
                                        public void onError(String code, String msg, @Nullable Throwable throwable) {
                                            callback.onError(code, msg, throwable);
                                        }
                                    });
                                }
                            }
                        })
                .show();
    }



}
