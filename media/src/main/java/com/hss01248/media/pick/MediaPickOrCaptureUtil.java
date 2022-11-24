package com.hss01248.media.pick;

import android.net.Uri;

import com.hss.utils.enhance.api.MyCommonCallback;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/11/2022 19:51
 * @Version 1.0
 */
public class MediaPickOrCaptureUtil {


    public static void start(MyCommonCallback<Uri> callback,boolean capture,String... mimeTypes){
        if(capture){
            if(MimeTypeUtil.hasImage(mimeTypes) && MimeTypeUtil.hasVideo(mimeTypes)){

            }
        }
    }
}
