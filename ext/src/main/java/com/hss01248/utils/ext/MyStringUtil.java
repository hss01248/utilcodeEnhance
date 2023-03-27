package com.hss01248.utils.ext;

import android.app.Activity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;

/**
 * @Despciption 不得使用com.blankj.utilcode.util.StringUtils
 * @Author hss
 * @Date 27/03/2023 19:39
 * @Version 1.0
 */
public class MyStringUtil {

    public static String getString(int stringId,Object... args){
        try {
            Activity activity = ActivityUtils.getTopActivity();
            if(activity !=null){
                return activity.getString(stringId,args);
            }
            return Utils.getApp().getString(stringId, args);
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return "string res not found: "+ stringId;
        }
    }
}
