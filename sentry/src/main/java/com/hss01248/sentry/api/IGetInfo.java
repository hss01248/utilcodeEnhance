package com.hss01248.sentry.api;

import android.app.Activity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.DeviceUtils;

import java.util.List;

public interface IGetInfo {

    String UID_NOT_LOGIN = "0";
    String NOT_SET = "not_set";

   default String uid(){
       return NOT_SET;
   }

    default String account(){
        return NOT_SET;
    }

    default  String deviceId(){
        return DeviceUtils.getAndroidID();
    }

    default String topPageName(){
        Activity topActivity = ActivityUtils.getTopActivity();
        if(topActivity !=null){
            return topActivity.getClass().getSimpleName();
        }
        return "no alive activity";
    }

    default String currentPageStack(){
        List<Activity> activityList = ActivityUtils.getActivityList();
        if(activityList ==null || activityList.isEmpty()){
            return "empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (Activity activity : activityList) {
                stringBuilder.append(activity.getClass().getSimpleName()).append(",");
            }
        }catch (Throwable throwable){
            stringBuilder.append(throwable.getMessage());
        }

        return stringBuilder.toString();
    }
}
