package com.hss01248.media.pick;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.blankj.utilcode.util.LogUtils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/11/2022 20:04
 * @Version 1.0
 */
public class MimeTypeUtil {

    public   static String buildMimeTypeWithDot(String[] acceptTypes) {
        if(acceptTypes == null || acceptTypes .length ==0){
            return "*/*";
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < acceptTypes.length; i++) {
            String type = acceptTypes[i];
            if(type.startsWith(".")){
                //兼容带.号 的type
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type.substring(1));
                if(TextUtils.isEmpty(type)){
                    LogUtils.w("unknown type ",type);
                    continue;
                }
            }
            str.append(type);
            if(i != acceptTypes.length-1){
                str.append(",");
            }
        }
        String st =  str.toString();
        if(TextUtils.isEmpty(st)){
            return "*/*";
        }
        return st;
    }

    /**
     * web传过来的有的是直接带.的文件后缀名
     * @param acceptTypes
     * @return
     */
    public  static String[] washMimeType(String[] acceptTypes) {
        if(acceptTypes == null || acceptTypes .length ==0){
            return acceptTypes;
        }
        String[] acceptTypes2 = new String[acceptTypes.length];
        for (int i = 0; i < acceptTypes.length; i++) {
            String type = acceptTypes[i];
            if(type.startsWith(".")){
                //兼容带.号 的type
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type.substring(1));
                if(TextUtils.isEmpty(type)){
                    LogUtils.w("unknown type ",type);
                    type = "*/*";
                }
            }
            acceptTypes2[i] = type;
        }
        return acceptTypes2;
    }

    public static boolean hasImage(String[] acceptTypes){
        if(acceptTypes == null || acceptTypes.length==0){
            return false;
        }
        acceptTypes = washMimeType(acceptTypes);
        for (String acceptType : acceptTypes) {
            if(acceptType.startsWith("image/")){
                return true;
            }
        }
        return false;
    }
    public static boolean hasVideo(String[] acceptTypes){
        if(acceptTypes == null || acceptTypes.length==0){
            return false;
        }
        acceptTypes = washMimeType(acceptTypes);
        for (String acceptType : acceptTypes) {
            if(acceptType.startsWith("video/")){
                return true;
            }
        }
        return false;
    }
    public static boolean hasAudio(String[] acceptTypes){
        if(acceptTypes == null || acceptTypes.length==0){
            return false;
        }
        acceptTypes = washMimeType(acceptTypes);
        for (String acceptType : acceptTypes) {
            if(acceptType.startsWith("audio/")){
                return true;
            }
        }
        return false;
    }
}
