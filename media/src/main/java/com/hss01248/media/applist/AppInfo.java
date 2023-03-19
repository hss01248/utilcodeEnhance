package com.hss01248.media.applist;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.annotations.SerializedName;


/**
 * create by zhangxiao on 20/04/23
 */


public class AppInfo {

    public static final int STATUS_FIRST_ADD = 0;
    public static final int STATUS_ADD = 1;
    public static final int STATUS_UPDATE = 2;
    public static final int STATUS_DELETE = 3;


    public transient long myDbId;


    public String userId="";

    /**
     * app名称
     */
    public String appName="";

    /**
     * 程序包名
     */
    public String packageName="";

    /**
     * 版本名
     */
    public String versionName="";

    /**
     * 版本号
     */
    public long versionCode;

    /**
     * 首次安装时间
     */
    public long firstInstallTime;

    /**
     * 最后更新时间
     */
    public long lastUpdateTime;

    /**
     * 是否系统应用。判断方式：packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
     */
    public int isSystemApp;

    /**
     * 上报状态
     * 0--》第一次上传
     * 1--》新增
     * 2--》更新
     * 3--》删除
     */
    @SerializedName("s")
    public int status;

    public boolean isSame(AppInfo info){
        if(info == null){
            return false;
        }
        try {

            if(!(packageName+"").equals(info.packageName+"")){
                return false;
            }
            if(!(versionName+"").equals(info.versionName+"")){
                return false;
            }
            if(!(appName+"").equals(info.appName+"")){
                return false;
            }
            if(versionCode != info.versionCode){
                return false;
            }
            if(lastUpdateTime != info.lastUpdateTime){
                return false;
            }
            if(firstInstallTime != info.firstInstallTime){
                return false;
            }
            if(isSystemApp != info.isSystemApp){
                return false;
            }

        }catch (Throwable throwable){
            LogUtils.e(throwable);
            LogUtils.w(this);
            return false;
        }

        return true;
    }


    public AppInfo doFork(){
        AppInfo info = new AppInfo();
        info.packageName = this.packageName;
        info.appName = this.appName;
        info.userId = this.userId;
        info.versionCode = this.versionCode;
        info.status = STATUS_UPDATE;
        info.versionCode = this.versionCode;
        info.versionName = this.versionName;
        info.firstInstallTime = this.firstInstallTime;
        info.lastUpdateTime = this.lastUpdateTime;
        info.isSystemApp = this.isSystemApp;
        return info;

    }




}
