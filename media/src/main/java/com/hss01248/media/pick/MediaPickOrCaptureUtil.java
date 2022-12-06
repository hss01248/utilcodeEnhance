package com.hss01248.media.pick;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.media.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.SimpleCallback;

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
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                })
                .asBottomList(StringUtils.getString(R.string.meida_pick_please_choose),
                        new String[]{ StringUtils.getString(R.string.meida_pick_take_photo),
                                StringUtils.getString(R.string.meida_pick_from_galerry)},
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
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                })
                .asBottomList(StringUtils.getString(R.string.meida_pick_please_choose),
                        new String[]{StringUtils.getString(R.string.meida_pick_record_audio),
                                StringUtils.getString(R.string.meida_pick_choose_audio_from_album)},
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
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                })
                .asBottomList(StringUtils.getString(R.string.meida_pick_please_choose),
                        new String[]{StringUtils.getString(R.string.meida_pick_record_video),
                                StringUtils.getString(R.string.meida_pick_record_video)},
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
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                })
                .asBottomList(StringUtils.getString(R.string.meida_pick_please_choose), new String[]{
                        StringUtils.getString(R.string.meida_pick_from_galerry),
                                StringUtils.getString(R.string.meida_pick_choose_video_from_album)},
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
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                })
                .asBottomList(StringUtils.getString(R.string.meida_pick_please_choose),
                        new String[]{StringUtils.getString(R.string.meida_pick_from_galerry),
                                StringUtils.getString(R.string.meida_pick_choose_video_from_album),
                                StringUtils.getString(R.string.meida_pick_take_photo),
                                StringUtils.getString(R.string.meida_pick_record_video)},
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
