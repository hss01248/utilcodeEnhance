package com.hss01248.crash;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 1.需要文件读写权限
 * 2.路径和子路径外部传入: 示例:
 *      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/crashlog/
 * 3.是否使用此工具交由外部判断
 *
 * Created by huangshuisheng on 2018/8/20.
 */

public class TheCrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    private static TheCrashHandler INSTANCE = new TheCrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> infos = new TreeMap<>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");

    public String getPath() {
        return path;
    }

    //String path = "/sdcard/lesongcrash/";
    String path;
   // IOpenText openText;

    private int mAppCount = 0;
    private AtomicBoolean isCrashing;
     WeakReference<Activity> topActivity;
     private boolean hasInit;

    private TheCrashHandler() {
    }

    public static TheCrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Application context){
        File di = context.getExternalFilesDir("crashlog");
        if(di == null){
            di = context.getFilesDir();
        }
        init(context,di.getAbsolutePath(),null);
    }

    @Deprecated
     void init(Application context,String logDir,IOpenText iOpenText) {
        mContext = context;
        path = logDir;
        if(hasInit){
            return;
        }
        hasInit = true;
        isCrashing = new AtomicBoolean(false);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        registerActivityLifecycleCallbacks(context);
    }

    private void registerActivityLifecycleCallbacks(Application context) {
        context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                topActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        try {
            isCrashing.set(true);
            handleException(ex);
        } catch (Exception var7) {
            Log.e("TheCrashHandler", "An error occurred in the uncaught exception handler", var7);
        } finally {
            Log.d("TheCrashHandler", "Crashlytics completed exception processing. Invoking default exception handler.");
            if(mDefaultHandler !=null){
                this.mDefaultHandler.uncaughtException(thread, ex);
                isCrashing.set(false);
            }else {
                try {
                    isCrashing.set(false);
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.e(TAG, "error : ", e);
                }
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);
            }
        }






        /*if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //打开日志文件
            try {
                ex.printStackTrace();
                Thread.sleep(15000);
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e(TAG, "error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }*/
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        collectDeviceInfo(mContext);
        String str = saveCrashInfo2File(ex);
        LogUtils.d("crashpath",str);
        //if(!TextUtils.isEmpty(str) && openText !=null){
            //openCrashLog(new File(str));
        //}
        TextFileDisplayActivity.launch(mContext,str);
        LogUtils.w("crash","",ex);
        return true;
    }

    private void openCrashLog(File file) {
        /*Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = openText.fileToUri(file);
        intent.setDataAndType(uri, "text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        mContext.startActivity(intent);*/
    }

    public   void openCrashLogDir(){
        //调用系统文件管理器打开指定路径目录
        /*
         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         XLogUtil.d("crashpath",path);
         Uri uri = openText.fileToUri(new File(path));
         XLogUtil.d("crashpath",uri.toString());
         intent.setDataAndType(uri, "");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        mContext.startActivity(intent);
         */

        ListFileActivity.launch(mContext,path);


    }

    String getCurrentActivityName() {
        if(topActivity != null && topActivity.get() != null){
            return topActivity.get().getClass().getSimpleName();
        }
        return "";
    }

     void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            infos.put("currentActivity", getCurrentActivityName());


            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("appVersionName", versionName);
                infos.put("appVersionCode", versionCode);
            }

            //硬件信息:
            infos.put("androidVersionName", DeviceUtils.getSDKVersionName());
            infos.put("androidVersionCode", DeviceUtils.getSDKVersionCode()+"");
            infos.put("androidId", DeviceUtils.getAndroidID()+"");
            infos.put("uniqueDeviceId", DeviceUtils.getUniqueDeviceId()+"");


        } catch (PackageManager.NameNotFoundException e) {
            //Log.e(TAG, "an error occured when collect package info", e);
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object object = field.get(null);
                if(object instanceof String[]){
                    String[] arr = (String[]) object;
                    infos.put(field.getName(), Arrays.toString(arr));
                }else{
                    infos.put(field.getName(), object+"");
                }

            } catch (Exception e) {
                Log.w(TAG, "an error occured when collect crash info", e);
                //e.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "  : " + value + "\n");
        }

        sb.append("\nCrash Info: \n\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            //long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "_i_" + getCurrentActivityName() +"-"+ex.getClass().getSimpleName() + ".txt";

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir,fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            //Log.e(TAG, "an error occured while writing file...", e);
            e.printStackTrace();

        }
        return null;
    }



    public interface IOpenText{


        /**
         * 注意7.0以上的文件uri兼容性
         */
        Uri fileToUri(File file);

    }

}
