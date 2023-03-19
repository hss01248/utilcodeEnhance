package com.hss01248.media.applist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;


import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**目前正是Android 11 马上要发布的时期，当前在Android手机上获取应用安装列表，要从三个维度上区分：获取方法、系统版本、是否系统应用。

 获取方法：至少有两种方法，它们的行为结果会有所差异
 系统版本：从 Android 11 开始，应用列表的获取将受到限制
 是否系统应用：系统应用与非系统应用的获取结果，也不尽相同

 作者：未子涵
 链接：https://www.jianshu.com/p/0fba8fd4a1e2
 来源：简书
 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 * create by zhangxiao on 20/04/24
 */
public class AppListUtils {


    public static int count;
    static int get_installed_applications_count;

    public static int getCount(){
        if(get_installed_applications_count > 0){
            if(count> 0 && count < get_installed_applications_count){
                count = get_installed_applications_count;
            }
        }
        if(count >0){
            return count;
        }
        getInstalledApksInfo(null,true);
        return count;
    }
    /**
     * 是否系统应用
     */
    public static boolean isSystemApp(PackageInfo packageInfo) {
        try {
            // 1
            if (new File("/data/app/" + packageInfo.packageName + ".apk").exists()) {
                // LogUtils.d(packageInfo.packageName + "/data/app/  exist()");
                return false;
            }
            // 2

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)
                    == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) {
                // Updated system app
                // LogUtils.d(packageInfo.packageName + " (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 uid:" + packageInfo.applicationInfo.uid);
                return true;
            }
            // 3
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                // LogUtils.d(packageInfo.packageName + "(packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 uid:" + packageInfo.applicationInfo.uid);
                return true;
            }
            //android系统的安装包uid是在区间 1000 ～ 9999 ，所以只要判断package的uid > 10000，就肯定不是系统应用
            //经测试,完全不准,国产rom各种改,让这个判断完全不能用
            /*if (packageInfo.applicationInfo.uid < 10000) {
                LogUtils.d(packageInfo.packageName + "  packageInfo.applicationInfo.uid :" + packageInfo.applicationInfo.uid);
                return true;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        //LogUtils.d(packageInfo.packageName + "  packageInfo.applicationInfo.uid :" + packageInfo.applicationInfo.uid);
        return false;
    }


    public static boolean isSystemApp2(ApplicationInfo packageInfo) {
        try {
            // 1
            if (new File("/data/app/" + packageInfo.packageName + ".apk").exists()) {
                // LogUtils.d(packageInfo.packageName + "/data/app/  exist()");
                return false;
            }
            // 2

            if ((packageInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)
                    == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) {
                // Updated system app
                // LogUtils.d(packageInfo.packageName + " (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 uid:" + packageInfo.applicationInfo.uid);
                return true;
            }
            // 3
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                // LogUtils.d(packageInfo.packageName + "(packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 uid:" + packageInfo.applicationInfo.uid);
                return true;
            }
            //android系统的安装包uid是在区间 1000 ～ 9999 ，所以只要判断package的uid > 10000，就肯定不是系统应用
            //经测试,完全不准,国产rom各种改,让这个判断完全不能用
            /*if (packageInfo.applicationInfo.uid < 10000) {
                LogUtils.d(packageInfo.packageName + "  packageInfo.applicationInfo.uid :" + packageInfo.applicationInfo.uid);
                return true;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        //LogUtils.d(packageInfo.packageName + "  packageInfo.applicationInfo.uid :" + packageInfo.applicationInfo.uid);
        return false;
    }

    public static List<AppInfo> getInstalledApksInfo() {
        return getInstalledApksInfo(null,false);
    }

    /**
     * 获取手机中所有已安装的应用
     */
    public static List<AppInfo> getInstalledApksInfo(AppCountInfo appCountInfo,boolean onlyforCount) {
        //获取手机中所有已安装的应用，并判断是否系统应用
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据，手机上安装的应用数据都存在appList里
        PackageManager packageManager = Utils.getApp().getPackageManager();

        List<PackageInfo> packages = new ArrayList<>();
        Map<String,PackageInfo> packageInfoMap = new HashMap<>();
        try {
            //printByFlag(packageManager,PackageManager.GET_ACTIVITIES,"GET_ACTIVITIES");
            // printByFlag(packageManager,PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES,"GET_ACTIVITIES | GET_SERVICES");

            List<PackageInfo> packagesByGids =  printByFlag(packageManager,PackageManager.GET_GIDS,"GET_GIDS");
            List<PackageInfo> packagesByInstruments =  printByFlag(packageManager,PackageManager.GET_INSTRUMENTATION,"GET_INSTRUMENTATION");
            if(packagesByGids != null && packagesByInstruments != null ){
                if(packagesByGids.size() == packagesByInstruments.size()
                        && packagesByGids.size() > 80){
                    //相等且大于80,可认为正常
                    packages = packagesByGids;
                    if(appCountInfo != null){
                        appCountInfo.pack_get_gids = packagesByGids.size();
                        appCountInfo.pack_get_instrumentation = packagesByInstruments.size();
                    }
                }else {
                    if(packagesByGids.size() == packagesByInstruments.size()){
                        LogUtils.w("dd",new GetApplistFailAndGetByFourApi("size less than 150"));
                    }else {
                        LogUtils.w("dd",new GetApplistFailAndGetByFourApi("size not same"));
                    }
                    packages = getByFourApi(packagesByGids,packagesByInstruments,packageManager,packageInfoMap,appCountInfo);
                }
            }else {
                LogUtils.w("dd",new GetApplistFailAndGetByFourApi("packagesByGids or ByInstruments is null"));
                packages = getByFourApi(packagesByGids,packagesByInstruments,packageManager,packageInfoMap,appCountInfo);
            }
        }catch (Throwable throwable){
            LogUtils.w("dd",new GetApplistCallFailException("getInstalledApksInfo fail first",throwable));
            //再获取一遍:
            packages = getByFourApi(null,null,packageManager,packageInfoMap,appCountInfo);
        }
        if(AppUtils.isAppDebug()){
            LogUtils.e("debug时再次获取,校对:");
            printByFlag(packageManager,0,"0");
            getInstallApp(packageManager);
        }

        if(onlyforCount){
            count = packages.size();
            return appList;
        }
        for (int i = 0; i < packages.size(); i++) {
            try {
                PackageInfo packageInfo = packages.get(i);
                //如需过滤,可开启
            /*if(packageInfo.packageName.contains("com.android.") || packageInfo.packageName.contains("com.google.")){
                continue;
            }*/
                AppInfo tmpInfo = new AppInfo();
                //tmpInfo.userId = DevicegetConfig().getUid();
                tmpInfo.packageName = packageInfo.packageName;
                tmpInfo.versionName = packageInfo.versionName;
                tmpInfo.versionCode = packageInfo.versionCode;
                tmpInfo.firstInstallTime = packageInfo.firstInstallTime;
                tmpInfo.lastUpdateTime = packageInfo.lastUpdateTime;
                tmpInfo.isSystemApp = AppListUtils.isSystemApp(packageInfo)?1:0;
                //此api非常耗时:会解压某些apk,从resource.arc里拿数据
                try {
                    tmpInfo.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }

                appList.add(tmpInfo);
            }catch (Throwable throwable){
                LogUtils.w("dd",throwable);
            }

        }
        count = appList.size();
        return appList;
    }

    private static List<PackageInfo> getByFourApi(List<PackageInfo> packagesByGids, List<PackageInfo> packagesByInstruments,
                                                  PackageManager packageManager, Map<String, PackageInfo> packageInfoMap, AppCountInfo appCountInfo) {

        List<PackageInfo> packages0 =   printByFlag(packageManager,0,"0");
        List<ApplicationInfo> applicationInfos = getInstallApp(packageManager);

        //饱和式救援
        if(packagesByGids == null){
            packagesByGids =  printByFlag(packageManager,PackageManager.GET_GIDS,"GET_GIDS");
        }

        if(packagesByInstruments == null){
            packagesByInstruments =  printByFlag(packageManager,PackageManager.GET_INSTRUMENTATION,"GET_INSTRUMENTATION");
        }

       if(packagesByGids != null && packagesByGids.size() >0){
           for (PackageInfo packagesByGid : packagesByGids) {
               packageInfoMap.put(packagesByGid.packageName,packagesByGid);
           }
           if(appCountInfo != null){
               appCountInfo.pack_get_gids = packagesByGids.size();
           }
       }
        if(packagesByInstruments != null && packagesByInstruments.size() >0){
            for (PackageInfo packagesByGid : packagesByInstruments) {
                packageInfoMap.put(packagesByGid.packageName,packagesByGid);
            }
            if(appCountInfo != null){
                appCountInfo.pack_get_instrumentation = packagesByInstruments.size();
            }
        }
        if(packages0 != null && packages0.size() >0){
            for (PackageInfo packagesByGid : packages0) {
                packageInfoMap.put(packagesByGid.packageName,packagesByGid);
            }
            if(appCountInfo != null){
                appCountInfo.pack_0 = packages0.size();
            }
        }
        int sizeapp = 0;
        if(applicationInfos != null){
            sizeapp = applicationInfos.size();
        }
        if(appCountInfo != null){
            appCountInfo.get_installed_applications = sizeapp;
        }
        get_installed_applications_count = sizeapp;
        if(packageInfoMap.size() >= sizeapp){
            //正常
        }else {
            if(appCountInfo != null){
                appCountInfo.get_pack_less_then_get_installed_applications = 1;
            }
            //异常
            LogUtils.w("applist",new GetApplistNumNotSameException("packageInfoMap.size():"+packageInfoMap.size()+",packageManager.getInstalledApplications:"+sizeapp));
        }

        List<PackageInfo> packages = new ArrayList<>();
        for (Map.Entry<String, PackageInfo> stringPackageInfoEntry : packageInfoMap.entrySet()) {
            packages.add(stringPackageInfoEntry.getValue());
        }
        return packages;
    }

    private static List<ApplicationInfo> getInstallApp(PackageManager packageManager) {
        try {
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(PackageManager.GET_SHARED_LIBRARY_FILES);
            //PackageManager.GET_SHARED_LIBRARY_FILES

            if(AppUtils.isAppDebug()){
                long size3 = 0;
                for (ApplicationInfo applicationInfo : applicationInfos) {
                    Parcel parcel2 = Parcel.obtain();
                    applicationInfo.writeToParcel(parcel2,0);
                    size3 += parcel2.dataSize();
                }

                LogUtils.e("getInstalledApplications appsize4-:"+ applicationInfos.size()
                        +", transfer size:"+(size3/1024)+"kb");
                ApplicationInfo applicationInfo = applicationInfos.get(0);
                //LogUtils.json(applicationInfo.metaData);
                // LogUtils.json(applicationInfo);
            }
            return applicationInfos;
        }catch (Throwable throwable){
            LogUtils.w("applist",new GetApplistCallFailException("getInstalledApplications(GET_SHARED_LIBRARY_FILES)",throwable));
            return null;
        }


    }

    private static List<PackageInfo> printByFlag(PackageManager packageManager, int flags, String get_meta_data) {
        try {
            List<PackageInfo> packages = packageManager.getInstalledPackages(flags);
            if(AppUtils.isAppDebug()){
                long size1 = 0;
                for (PackageInfo aPackage : packages) {
                    Parcel parcel = Parcel.obtain();
                    aPackage.writeToParcel(parcel,0);
                    size1+=parcel.dataSize();
                }
                LogUtils.e(" getInstalledPackages("+get_meta_data+") appsize1:"+packages.size()+",transfer size:"+(size1/1024)+"kb");
            }
            return packages;
        }catch (Throwable throwable){
            LogUtils.w("applist",new GetApplistCallFailException("getInstalledPackages:"+get_meta_data,throwable));
            return null;
        }

        //LogUtils.json(packages.get(packages.size()/2));
    }

    // 通过packName得到PackageInfo，作为参数传入即可
    private boolean isSystemApp2(PackageInfo pi) {
        boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp || isSysUpd;
    }

    /**
     * 匹配publicinfo中的包名信息
     * @param content appinfo的publicinfo属性
     * @return  匹配到的包名信息
     */
    private static String getPackagePub(String content) {
        String str = "(?<=/)[0-9a-zA-Z\\-_.]*?(?===/base.apk)";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(content);
        Set<String> ids = new HashSet<>();
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 获取手机中的所有包含publicinfo的publicinfo包名列表
     * @param packageManager
     * @return 返回所有包含publicinfo的publicinfo包名列表
     */
    public static String getPackagePubList(PackageManager packageManager) {
        int pubCount = 0;
        String packInfoListString = "";
        Set<String> packageInfoSet = new HashSet<>();
        try {
            List<PackageInfo> packages = packageManager.getInstalledPackages(0);
            Collections.sort(packages, new Comparator<PackageInfo>() {
                @Override
                public int compare(PackageInfo packageInfo, PackageInfo t1) {
                    return t1.packageName.compareTo(packageInfo.packageName);
                }
            });
            for (PackageInfo packageInfo : packages) {
                try {
                    String publicSource = packageInfo.applicationInfo.publicSourceDir;
                    if (publicSource != null && publicSource.length() > 0) {
                        String result = getPackagePub(publicSource);
                        if (result != null) {
                            // 包名的后缀随机串要大于10;
                            if (result.length() - result.indexOf("-") > 10 && pubCount < 100) {
                                pubCount++;
                                packageInfoSet.add(result);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            packInfoListString = Arrays.toString(packageInfoSet.toArray());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return packInfoListString;
    }
}
