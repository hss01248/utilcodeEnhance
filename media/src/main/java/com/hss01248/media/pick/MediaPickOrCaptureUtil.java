package com.hss01248.media.pick;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.iwidget.singlechoose.SingleChooseDialogImpl;
import com.hss01248.iwidget.singlechoose.SingleChooseDialogListener;
import com.hss01248.media.R;

import java.io.File;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/11/2022 19:51
 * @Version 1.0
 */
public class MediaPickOrCaptureUtil {

    public static void pickImageOrTakePhoto(boolean useFrontCamera,MyCommonCallback<Uri> callback){
        new SingleChooseDialogImpl().showAtBottom(
                StringUtils.getString(R.string.meida_pick_please_choose),
                new CharSequence[]{ StringUtils.getString(R.string.meida_pick_take_photo),
                        StringUtils.getString(R.string.meida_pick_from_galerry)},
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
                        LogUtils.d(text+","+position);
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

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                });
    }

    public static void pickOrRecordAudio(MyCommonCallback<Uri> callback){
        new SingleChooseDialogImpl().showAtBottom(
                StringUtils.getString(R.string.meida_pick_please_choose),
                new CharSequence[]{
                        StringUtils.getString(R.string.meida_pick_record_audio),
                        StringUtils.getString(R.string.meida_pick_choose_audio_from_album)
                },
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
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

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                });
    }

    public static void pickOrRecordVideo(boolean useFrontCamera,int maxDurationInSecondOfVideo,MyCommonCallback<Uri> callback){

        new SingleChooseDialogImpl().showAtBottom(
                StringUtils.getString(R.string.meida_pick_please_choose),
                new CharSequence[]{
                        StringUtils.getString(R.string.meida_pick_record_video),
                        StringUtils.getString(R.string.meida_pick_choose_video_from_album)
                },
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
                        if(position ==0){
                            CaptureVideoUtil.startVideoCapture(useFrontCamera,maxDurationInSecondOfVideo,
                                    1024*1024*1024,new MyCommonCallback<String>() {
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

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                });
    }

    public static void pickImageOrVideo(MyCommonCallback<Uri> callback){
        new SingleChooseDialogImpl().showAtBottom(
                StringUtils.getString(R.string.meida_pick_please_choose),
                new CharSequence[]{
                        StringUtils.getString(R.string.meida_pick_from_galerry),
                        StringUtils.getString(R.string.meida_pick_choose_video_from_album)
                },
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
                        if(position ==0){
                            MediaPickUtil.pickImage(callback);
                        }else if(position ==1){
                            MediaPickUtil.pickVideo(callback);
                        }
                    }

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                });
    }
    public static void pickOrCaptureImageOrVideo(boolean useFrontCamera,int maxDurationInSecondOfVideo,MyCommonCallback<Uri> callback){
        new SingleChooseDialogImpl().showAtBottom(
                StringUtils.getString(R.string.meida_pick_please_choose),
                new CharSequence[]{
                        StringUtils.getString(R.string.meida_pick_from_galerry),
                        StringUtils.getString(R.string.meida_pick_choose_video_from_album),
                        StringUtils.getString(R.string.meida_pick_take_photo),
                        StringUtils.getString(R.string.meida_pick_record_video)
                },
                new SingleChooseDialogListener(){

                    @Override
                    public void onItemClicked(int position, CharSequence text) {
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

                    @Override
                    public void onCancel(boolean fromBackPressed, boolean fromOutsideClick, boolean fromCancelButton) {
                        SingleChooseDialogListener.super.onCancel(fromBackPressed, fromOutsideClick, fromCancelButton);
                        callback.onError(StringUtils.getString(R.string.meida_pick_canceled));
                    }
                });
    }



}
