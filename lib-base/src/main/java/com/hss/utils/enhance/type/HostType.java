package com.hss.utils.enhance.type;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


/**
 * by hss
 * data:2020-04-26
 * desc:
 */
public class HostType {

    public static final int TYPE_RELEASE = 0;
    public static final int TYPE_DEV = 1;
    public static final int TYPE_YAPI = 2;
    public static final int TYPE_TEST = 3;
    public static final int TYPE_SIT = 4;
    public static final int TYPE_PRE_RELEASE = 5;
    public static final int TYPE_CUSTOM = 6;

    static Map<Integer, String> baseUrls = new HashMap<Integer, String>();
    static Map<Integer, String> baseWebUrls = new HashMap<Integer, String>();
    static Map<Integer, String> descs = new HashMap<Integer, String>();

    public static int getCurrentType() {
        return currentType;
    }

    static int currentType;
    static final String KEY = "urlType";
    static Application app;

    /**
     * 自动初始化,无需再调用. 调用也无妨,后一次调用会自动覆盖前一次传入的值
     * @param app
     * @param buildType
     */
    @Deprecated
    public  static void init(Application app,String buildType) {
        BuildType.init(app,buildType);

        HostType.app = app;
        if (isReallyRelease()) {
            currentType = TYPE_RELEASE;
        } else {
            currentType = getInt(app, KEY, getDefaultUrlType());
        }
        initDesc();
    }



    /**
     *
     * @param type 取值为UrlType.TYPE_xxx
     * @param baseUrl
     */
    public static void setBaseUrl(int type, String baseUrl) {
        baseUrls.put(type, baseUrl);
    }

    /**
     *
     * @param type 取值为UrlType.TYPE_xxx
     * @param baseUrl
     */
    public static void setBaseWebUrl(int type, String baseUrl) {
        baseWebUrls.put(type, baseUrl);
    }

    public static String getBaseUrl() {
        return baseUrls.get(currentType);
    }

    public static String getWebBaseUrl() {
        return baseWebUrls.get(currentType);
    }

    public  static boolean isReallyRelease() {
        return (!isAppDebug()) && (BuildType.isRelease() || BuildType.isMultichannel());
    }

    private static int getDefaultUrlType() {
        int type;
        if (BuildType.isDebug()) {
            type = TYPE_DEV;
        } else if (BuildType.isCommon()) {
            type = TYPE_TEST;
        } else if (BuildType.isRelease()) {
            type = TYPE_RELEASE;
        } else {
            type = TYPE_RELEASE;
        }
        return type;
    }

    public   static boolean isAppDebug() {
        ApplicationInfo ai = app.getApplicationInfo();
        return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private static void initDesc() {
        descs.clear();
        descs.put(TYPE_RELEASE, "正式服");
        descs.put(TYPE_DEV, "本地/开发环境");
        descs.put(TYPE_YAPI, "YAPI");
        descs.put(TYPE_TEST, "测试环境");
        descs.put(TYPE_SIT, "集成环境");
        descs.put(TYPE_PRE_RELEASE, "预发布");
        descs.put(TYPE_CUSTOM, "自定义");
    }

    public static void showChangeHostDialog(final Activity activity) {
        String[] types = new String[7];
        //ArrayList<String> texts = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
            String text = descs.get(i);
            String isCurrent = currentType == i ? "(now)" : "";
            String baseUrl = baseUrls.get(i);
            //String webUrl = baseWebUrls.get(i);
            text = text + isCurrent + "\n" + baseUrl ;
            types[i] = text;
        }

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setSingleChoiceItems(types, currentType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == currentType) {
                            return;
                        }
                        if(TextUtils.isEmpty(baseUrls.get(which))){
                            Toast.makeText(activity, "此环境的baseUrl未设置,不可切换", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        putIntNow(activity.getApplication(), KEY, which);
                        Toast.makeText(activity, "已保存,3s后重启生效", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        restartApp(activity.getApplication());
                    }
                })
                .setTitle("切换环境/域名\n点击切换后,自动重启app,然后即可生效")
                //.setMessage("点击切换后,自动重启app,然后即可生效")
                //.setNegativeButton("取消",null)
                .create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public  static void restartApp(final Context context) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, getFlags2() );
                    AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }

            }
        }, 3000);
    }

    static int getFlags2(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.FLAG_ONE_SHOT;
    }

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences("urltypeInfo", Context.MODE_PRIVATE);
    }

    static boolean putIntNow(Context context, String key, int val) {
        return getSP(context).edit().putInt(key, val).commit();
    }

    static int getInt(Context context, String key, int defVal) {
        return getSP(context).getInt(key, defVal);
    }


     static boolean isRelease() {
        return currentType == TYPE_RELEASE;
    }
}
