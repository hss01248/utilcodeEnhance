package com.hss.utils.enhance.foregroundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.R;
import com.hss01248.permission.ext.IExtPermissionCallback;
import com.hss01248.permission.ext.MyPermissionsExt;
import com.hss01248.permission.ext.permissions.NotificationPermission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @Despciption todo
 * @Author hss
 * @Date 23/10/2023 10:54
 * @Version 1.0
 */
public class CommonProgressService extends Service {


    static final int notify_id = 199;
    public  static void startS(String title,String msg,int notifyId,Runnable runnable){
        MyPermissionsExt.askPermission(ActivityUtils.getTopActivity(),
                new NotificationPermission(),
                new IExtPermissionCallback() {
                    @Override
                    public void onGranted(String name) {
                        Intent intent = new Intent(ActivityUtils.getTopActivity(), CommonProgressService.class);
                        intent.putExtra("title",title);
                        intent.putExtra("msg",msg);
                        intent.putExtra("notifyId",notifyId);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //8.0后才支持
                            ActivityUtils.getTopActivity().startForegroundService(intent);
                        } else {
                            ActivityUtils.getTopActivity().startService(intent);
                        }
                        runnable.run();
                    }

                    @Override
                    public void onDenied(String name) {
                        ToastUtils.showLong("必须有通知权限才能开始后台下载,避免进程被杀");
                    }
                });


    }

    public static void updateProgress(int progress,int max,String title,String msg,int notifyId){
        NotificationManager mNotificationManager = (NotificationManager) Utils.getApp().getSystemService(NOTIFICATION_SERVICE);
        if(notifyId<=0){
            notifyId = notify_id;
        }
        if(progress == max && progress >0){
        //无法取消
            //mNotificationManager.cancel(notify_id);
            mNotificationManager.notify(notifyId,getNotification(Utils.getApp(), title, progress+"/"+max+"(完成)",progress,max));
            //mNotificationManager.cancel(notifyId);
        }else {
            mNotificationManager.notify(notifyId,getNotification(Utils.getApp(), title, msg,progress,max));
        }


        //mNotificationManager.getno
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       // LogUtils.d("on onBind: ","------>");
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //调用startForeground() 方法
        //LogUtils.d("onStartCommand: ","------>");
        if(intent !=null){
            String title = intent.getStringExtra("title");
            String msg = intent.getStringExtra("msg");
            int notifyId = intent.getIntExtra("notifyId",-1);
            if(notifyId<=0){
                notifyId = notify_id;
            }
            //前台服务类型（foregroundServiceType）是在 Android 10 引入的, 14开始强制要求
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                startForeground(notifyId, getNotification(ActivityUtils.getTopActivity(), title, msg,-1,100));//创建一个通知，创建通知前记得获取开启通知权限
            }else {
                startForeground(notifyId,
                        getNotification(ActivityUtils.getTopActivity(), title, msg,-1,100),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);//创建一个通知，创建通知前记得获取开启通知权限
            }

        }
        //doTask();

        return super.onStartCommand(intent, flags, startId);
    }

    public static void doTask() {
        ThreadUtils.executeByCpu(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                boolean bo = 1==1;
                long count = 0;
                while (bo){
                    count++;
                    System.out.println("count: "+count);
                    Thread.sleep(500);
                }
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }

    public static void doHttpTask() {
        ThreadUtils.executeByCpu(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                boolean bo = 1==1;
                long count = 0;
                while (bo){
                    count++;
                    System.out.println("http count: "+count);
                    InputStreamReader in = null;
                    try{
                        URL url = new URL("https://www.baidu.com/404xx.html");
                        //使用HttpURLConnection打开连接
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        //得到读取的内容(流)
                         in = new InputStreamReader(urlConn.getInputStream());
                        // 为输出创建BufferedReader
                        BufferedReader buffer = new BufferedReader(in);
                        String inputLine = null;
                        String resultData = "";
                        //使用循环来读取获得的数据
                        while (((inputLine = buffer.readLine()) != null))
                        {
                            //我们在每一行后面加上一个"\n"来换行
                            resultData += inputLine + "\n";
                        }
                        System.out.println("http result: \n"+resultData);
                        //关闭InputStreamReader
                        //in.close();
                        //关闭http连接
                        urlConn.disconnect();
                    }catch (Throwable throwable){
                        LogUtils.w("https://www.baidu.com/404xx.html",throwable);
                    }finally {
                        if(in != null){
                            try{
                                in.close();
                            }catch (Throwable throwable2){
                                throwable2.printStackTrace();
                            }

                        }

                    }
                    Thread.sleep(500);
                }
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //LogUtils.d("on onCreate: ","------>");
        //startForeground(1, null);//不创建通知
    }

    private static Notification getNotification(Context context, String title, String text,int progress,int max) {
        boolean isSilent = true;//是否静音
        boolean isOngoing = true;//是否持续(为不消失的常驻通知)
        String channelName = "服务常驻通知";
        String channelId = "35565";
        String category = Notification.CATEGORY_SERVICE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.areNotificationsEnabled()
        //ActivityUtils.getTopActivity()可能为空
        Intent nfIntent = null;
        if(ActivityUtils.getTopActivity() != null){
            nfIntent = new Intent(context, ActivityUtils.getTopActivity().getClass());
        }else {
            nfIntent = new Intent(Intent.ACTION_VIEW);
        }
        int  mFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nfIntent, mFlag);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {
            //安卓8.0以上系统要求通知设置Channel,否则会报错
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);//锁屏显示通知
            channel.setSound(null, null); // 设置推送通知之时的铃声。null表示静音推送
            channel.enableLights(false); // 通知渠道是否让呼吸灯闪烁
            channel.enableVibration(false); // 通知渠道是否让手机震动
            channel.setShowBadge(false); // 通知渠道是否在应用图标的右上角展示小红点
            // VISIBILITY_PUBLIC显示所有通知内容，Notification.VISIBILITY_PRIVATE只显示标题，Notification.VISIBILITY_SECRET不显示任何内容
           // channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE); // 设置锁屏时候的可见性
            channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT); // 设置通知渠道的重要性级别
            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentIntent(pendingIntent) //设置PendingIntent
                .setSmallIcon(R.drawable.ic_launcher) //设置状态栏内的小图标
                .setContentTitle(title) //设置标题
                .setContentText(text) //设置内容
                .setWhen(System.currentTimeMillis())
                // from a lock screen.
               // .setAuthenticationRequired(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//设置通知公开可见
                .setOngoing(isOngoing)//设置持续(不消失的常驻通知)
                .setCategory(category)//设置类别
                .setProgress(max, Math.max(progress, 0), progress == -1)
                //8)setProgress(int max, int progress,boolean indeterminate)
                //属性：max:进度条最大数值 、progress:当前进度、indeterminate:表示进度是否不确定，true为不确定，false为确定
                .setPriority(NotificationCompat.PRIORITY_MAX);//优先级为：重要通知
       // builder.setChannelId(channelId);
        return builder.build();
    }
}
