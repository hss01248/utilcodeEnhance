package com.hss.utils.enhance.intent;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.R;

import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.openuri.OpenUri;
import com.hss01248.toast.MyToast;


import java.io.File;
import java.util.List;



/**
 * by hss
 * data:2020-04-01
 * desc:
 */
public class SysIntentUtil {

    public static void dial(String phoneNum){
        if (phoneNum.startsWith("tel:")) {//调用系统拔号
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNum));
            ActivityUtils.getTopActivity().startActivity(intent);
        }else {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNum));
            ActivityUtils.getTopActivity().startActivity(intent);
        }
    }

    public static void openBrowser(String url){
        openUrlBySysBrowser(url,ActivityUtils.getTopActivity(),true);
    }

    public static void goSysSetting(){
        Intent mItent=new Intent(Settings.ACTION_SETTINGS);
        ActivityUtils.getTopActivity().startActivity(mItent);
    }

    public static void goAppSettings(){
        Activity context = ActivityUtils.getTopActivity();
            Intent mIntent = new Intent();
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                mIntent.setAction(Intent.ACTION_VIEW);
                mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
            context.startActivity(mIntent);


    }

    public static void chooseContact(Activity activity, MyCommonCallback<String> callback){

    }

    public static void sendSms(String smsBody){
        Uri uri = Uri.parse("sms:");
        Intent intent = new Intent();
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_TEXT, smsBody);
        intent.putExtra("sms_body", smsBody);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_SENDTO);
            String defaultSmsPackageName = Telephony.Sms
                    .getDefaultSmsPackage(Utils.getApp());
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
        }
        try {
            ActivityUtils.getTopActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //ExceptionReporterHelper.reportException(e);
        }

    }

    public static void goToNotificationSettingsPage() {

    }

    /** 分享到短信
     * @param phone
     * @param msg
     */
    public void shareToSms(String phone, String msg) {
        try {
            Uri smsToUri = Uri.parse("smsto:" + phone);
            Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            mIntent.putExtra("sms_body", msg);
            ActivityUtils.getTopActivity().startActivity(mIntent);
        } catch (Exception e) {
            ToastUtils.showLong("no sms app installed");
        }
    }

    public static void openFile(Uri uri){
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            OpenUri.addPermissionRW(intent);
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            intent.setDataAndType(uri,type);
            ActivityUtils.startActivity(intent);
        }catch (Throwable throwable){
            MyToast.error(StringUtils.getString(R.string.open_no_activity));
        }
    }

    public static void openFile(String filePath){
        Uri uri = OpenUri.fromFile(Utils.getApp(),new File(filePath));
        LogUtils.d("uri to open :"+ uri.toString());
        openFile(uri);
    }

    public static void shareToEmail(String emailBody) {
        try {
            // 发送一封的email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // 设置文本格式
            emailIntent.setType("text/plain");
            // 设置对方邮件地址
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
            // 设置标题内容
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            // 设置邮件文本内容
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
            ActivityUtils.getTopActivity().startActivity(Intent.createChooser(emailIntent, null));
        } catch (Exception e) {
            ToastUtils.showLong("email not installed"  );
        }
    }

    /**
     * 进入play store去下载包名对应的app
     *
     * @param packageName 包名
     */
    public static void goAppMarket(String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName));
            intent.setPackage("com.android.vending");
            ActivityUtils.getTopActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                Uri uri = Uri.parse("market://details?id=" + packageName);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                ActivityUtils.getTopActivity().startActivity(intent);
            }catch (Exception e1){
                e1.printStackTrace();
                MyToast.show("Please download a app market");
            }


        }
    }

    public static void openMapNavi(double lat,double lng){
        if (AppUtils.isAppInstalled("com.google.android.apps.maps")){
            openGoogleNavi(lat,lng);
//                openGoogleNavi(22.5428750360,114.0595699327);
        } else {
            //没有谷歌地图打开网页版导航
            openWebGoogleNavi(lat, lng);
//                openWebGoogleNavi(22.5428750360,114.0595699327);
        }
    }

    /**
     * 打开google地图客户端开始导航
     * q:目的地
     * mode：d驾车 默认
     */
    private static void openGoogleNavi(double lat,double lng) {
        StringBuffer stringBuffer = new StringBuffer("google.navigation:q=").append(lat).append(",").append(lng).append("&mode=d");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
    /**
     * 打开google Web地图导航
     */
    private static void openWebGoogleNavi(double lat,double lng) {
        StringBuffer stringBuffer = new StringBuffer("https://www.google.com/maps/dir/?api=1&destination=").append(lat).append(",").append(lng);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        if (IntentUtils.isIntentAvailable(intent)){
            startActivity(intent);
        }else {
            //没有浏览器  也没有Google map app
            //弹出提示
            ToastUtils.showLong(R.string.no_browser);
        }
    }

    public static void openUrlBySysBrowser(String url, Activity activity, boolean toastIfNotFound) {

        if (TextUtils.isEmpty(url)) {
            if (toastIfNotFound) {
                MyToast.show("url is empty");
            }
            return;
        }
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            if (toastIfNotFound) {
                MyToast.show("no browser");
            }
        }
    }

    public static void shareByPackageName(Context context, String shareStr, String packageName,
                                          String name) {
        boolean isInstalledApp = isInstalledByPackageName(context, packageName);
        if (isInstalledApp) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(Intent.EXTRA_TEXT, shareStr);
                intent.setPackage(packageName);
                ActivityUtils.startActivity(intent);
            } catch (Exception e) {
                MyToast.show(StringUtils.getString(R.string.share_app_not_install));
            }
        } else {
            MyToast.show(StringUtils.getString(R.string.share_app_not_install));
        }
    }

    public static boolean isInstalledByPackageName(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            Context outContext = context
                    .createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            return outContext != null;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public static void killAppProcess() {
        //会导致app重启
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList) {
            if (runningAppProcessInfo.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void killApp(){
        List<Activity> activityList = ActivityUtils.getActivityList();
        try {
            for (Activity activity : activityList) {
                try {
                    activity.finish();
                }catch (Throwable throwable){

                }

            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

    }

    /*private void shareByPackageNameWithoutPic(String packageName, ShareCallBack callBack) {
        String shareContent = shareInfo.getShareContentTitle() + "\n" + shareInfo
                .getShareContentDescription() + "\n" + shareInfo.getShareContentUrl();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.setPackage(packageName);
        try {
            activity.startActivityForResult(intent, SHARE_BY_WHAT_INTENT);
        } catch (Exception e) {
            callBack.onFail(e);
        }

    }*/


}
