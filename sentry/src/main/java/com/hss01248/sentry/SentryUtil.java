package com.hss01248.sentry;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.sentry.Breadcrumb;
import io.sentry.Hint;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.android.core.SentryAndroid;
import io.sentry.protocol.Device;
import io.sentry.protocol.Message;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.User;

/**
 * function: Sentry上报
 *
 * @author zy
 * @since 2020-04-15
 */
@SuppressWarnings({"unused", "Convert2Lambda"})
public class SentryUtil {

    public static final String TAG = "SentryUtil";
    public static final String ENV_TEST = "test-android";
    public static final String ENV_RELEASE = "release-android";

    public static IGetInfo getIGetInfo() {
        return iGetInfo;
    }

    public static void setIGetInfo(IGetInfo iGetInfo) {
        SentryUtil.iGetInfo = iGetInfo;
    }

    static  IGetInfo iGetInfo = new DefaultGetInfo();

    /**
     * 业务名
     */
    private final String businessName;
    /***
     * 设置消息
     */
    private final String msg;
    /**
     * 日志级别
     */
    private final int level;
    /**
     * 额外的信息,丰富msg的背景信息
     */
    private final Map<String, Object> extras;
    /**
     * 额外的tag,用作过滤
     */
    private final Map<String, String> tags;
    private Throwable throwable;



    private SentryUtil(Builder builder) {
        businessName = builder.businessName;
        msg = builder.msg;
        level = builder.level;
        extras = builder.extras;
        tags = builder.tags;
        throwable = builder.throwable;
    }



    /*@Override
    public void reportException(Throwable o) {
        if (sentryClient != null) {
            //EventBuilder eventBuilder = buildEventBuilder();

            sentryClient.captureException(o);
        }
        Sentry.captureException(o);

    }*/

    public static class Builder {
        private String businessName;
        private String msg;
        private int level = Log.INFO;
        private final Map<String, Object> extras = new HashMap<>();
        private final Map<String, String> tags = new HashMap<>();
        private Throwable throwable;


        /**
         * @param businessName businessName
         */
        public Builder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder exception(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        /**
         * @param msg msg
         */
        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 日志级别
         *
         * @param level 取值Android.util.Log中的几个值
         */
        public Builder level(int level) {
            this.level = level;
            return this;
        }

        /**
         * 添加额外的信息,已丰富msg的背景信息
         *
         * @param key   key
         * @param value value
         */
        public Builder addExtra(String key, Object value) {
            this.extras.put(key, value);
            return this;
        }

        /**
         * 添加额外的tag,用作过滤
         *
         * @param key   key
         * @param value value
         */
        public Builder addTag(String key, String value) {
            this.tags.put(key, value);
            return this;
        }


        public SentryUtil build() {
            return new SentryUtil(this);
        }

        public void doReport() {
             new SentryUtil(this).report();
        }
    }

    public static Builder create(){
        return new Builder();
    }


    static boolean isDebug;
    /***
     * sentry上报初始化
     * @param applicationContext Application上下文
     */
    public static void init(Application applicationContext,boolean debug) {
        isDebug = debug;
        SentryAndroid.init(applicationContext, options -> {
            //options.setDsn(dsn);
            options.setAnrEnabled(true);
            options.setAnrReportInDebug(true);
            options.setDebug(debug);
            options.setEnableActivityLifecycleBreadcrumbs(debug);
            options.setEnableAutoActivityLifecycleTracing(debug);
            options.setEnableSystemEventBreadcrumbs(debug);
            options.setEnableActivityLifecycleTracingAutoFinish(debug);
            options.setEnableNdk(true);
            options.setBeforeBreadcrumb(new SentryOptions.BeforeBreadcrumbCallback() {
                @Override
                public @Nullable
                Breadcrumb execute(@NonNull Breadcrumb breadcrumb, @NonNull Hint hint) {
                    if(debug){
                        Log.d("Breadcrumb",Thread.currentThread().getName() +","+breadcrumb+"");
                    }
                    breadcrumb.getData().clear();
                    return breadcrumb;
                }
            });
            options.setBeforeSend(new SentryOptions.BeforeSendCallback() {
                @Override
                public @Nullable SentryEvent execute(@NonNull SentryEvent event, @NonNull Hint hint) {
                    if(debug){
                        Log.d("BeforeSend",Thread.currentThread().getName() +","+event+"");
                    }
                    //10k->5k->3k->2k
                    if(event.getThreads() != null){
                        if(event.getThrowable() == null){
                            event.getThreads().clear();
                        }
                    }
                    if(event.getBreadcrumbs() != null){
                        if(ENV_RELEASE.equals(event.getEnvironment())){
                            event.getBreadcrumbs().clear();
                        }
                    }
                    event.setTag("manufacturer",Build.MANUFACTURER);
                    event.setTag("brand",Build.BRAND);
                    event.setTag("model",Build.MODEL);
                    event.setTag("isBackground", !AppUtils.isAppForeground()+"");
                    event.setTag("network", network());

                    if(SentryUtil.getIGetInfo() != null){
                        event.setTag("topPage", SentryUtil.getIGetInfo().topPageName());
                        boolean unlogin = IGetInfo.UID_NOT_LOGIN.equals(SentryUtil.getIGetInfo().uid())
                                || "".equals(SentryUtil.getIGetInfo().uid());
                        if(unlogin){
                            event.setTag("uid", "unlogin");
                        }else {
                            event.setTag("uid", SentryUtil.getIGetInfo().uid());
                        }

                        event.setModule("pageStack", SentryUtil.getIGetInfo().currentPageStack());

                        event.setTag("account", SentryUtil.getIGetInfo().account());
                        String uid = SentryUtil.getIGetInfo().uid();
                        if(!IGetInfo.NOT_SET.equals(uid) && !unlogin){
                            User user = new User();
                            user.setId("unlogin");
                            user.setUsername(SentryUtil.getIGetInfo().account());
                            event.setUser(user);
                        }
                        event.setTag("deviceId", SentryUtil.getIGetInfo().deviceId());
                    }

                    //去掉context里的device,3K->2K
                    Device device = new Device();
                    device.setBrand(Build.BRAND);
                    device.setModel(Build.MODEL);
                    device.setManufacturer(Build.MANUFACTURER);
                    event.getContexts().setDevice(device);

                    //event.getContexts().setOperatingSystem(new OperatingSystem());
                    //event.setSdk(new SdkVersion());

                    //添加公共tag: device brand,fammily,network,isbackground, threadname


                    return event;
                }
            });
        });
    }

    @SuppressLint("MissingPermission")
    static String network(){
        try {
            return NetworkUtils.getNetworkType().name().substring(8).toLowerCase();
        }catch (Throwable throwable){
            if(isDebug){
                throwable.printStackTrace();
            }
            return "unknown-"+throwable.getClass().getSimpleName();
        }
    }


    /**
     * 使用EventBuilder构建事件上报
     * Sentry事件上报
     */
    public void report() {

  /*      if(throwable != null){
            if(ReporterContainer.shouldNotReport(throwable)){
                return;
            }
        }else if(!TextUtils.isEmpty(msg)){
            if(ReporterContainer.shouldNotReport(msg)){
                return;
            }
        }*/

        Sentry.captureEvent(buildEventBuilder());
    }


    /**
     * 构建EventBuilder
     */
    private SentryEvent buildEventBuilder() {
        SentryEvent eventBuilder = new SentryEvent();
        if(throwable != null){
            eventBuilder.setThrowable(throwable);
        }else {
            Message msg2 = new Message();
            msg2.setMessage(msg);
            eventBuilder.setMessage(msg2);
            eventBuilder.setTag("business", businessName);
            eventBuilder.setTag("isBusiness", "true");
            eventBuilder.setLevel(level(level));
        }



        //额外的tag,不能直接设,会覆盖globaltag
        if (tags != null && !tags.isEmpty()) {
            Set<Map.Entry<String, String>> entry = tags.entrySet();
            for (Map.Entry<String, String> en : entry) {
                eventBuilder.setTag(en.getKey(), en.getValue());
            }
        }

        if (extras != null && !extras.isEmpty()) {
            Set<Map.Entry<String, Object>> entry = extras.entrySet();
            for (Map.Entry<String, Object> en : entry) {
                eventBuilder.setExtra(en.getKey(), en.getValue());
            }
        }
        eventBuilder.setEnvironment(isDebug ? ENV_TEST:ENV_RELEASE);
        return eventBuilder;
    }

    /**
     * 获取日志的级别
     *
     * @param logLevel logLevel
     * @return Event.Level
     */
    private SentryLevel level(int logLevel) {
        switch (logLevel) {
            case Log.VERBOSE:
            case Log.DEBUG:
                return SentryLevel.DEBUG;
            case Log.WARN:
                return SentryLevel.WARNING;
            case Log.ERROR:
                return SentryLevel.ERROR;
            case Log.ASSERT:
                return SentryLevel.FATAL;
            default:
                return SentryLevel.INFO;
        }
    }

    /*=========================================一些静态方法========================================*/
    /**
     * Sentry事件上报
     *
     * @param o 异常事件
     */
    public static void report(Object o) {
        try {
           /* if(ReporterContainer.shouldNotReport(o)){
                return;
            }*/
                if (o instanceof SentryEvent) {
                    Sentry.captureEvent((SentryEvent) o);
                }  else if(o instanceof String){
                    Message message = new Message();
                    message.setMessage((String) o);
                    SentryEvent event = new SentryEvent();
                    event.setMessage(message);
                    event.setEnvironment(isDebug ? ENV_TEST:ENV_RELEASE);
                    Sentry.captureEvent(event);
                }else if(o instanceof Throwable){
                    SentryEvent event = new SentryEvent();
                    event.setThrowable((Throwable) o);
                    event.setEnvironment(isDebug ? ENV_TEST:ENV_RELEASE);
                    Sentry.captureEvent(event);
                }else {
                    //打印
                    if(isDebug){
                        Log.d("SentryUtil", "sendSentryException: error:"+ o);
                    }

                }
        }catch (Throwable throwable){
            if(isDebug){
                throwable.printStackTrace();
            }

        }

    }

    /**
     * 设置全局的tag信息 一些自定义的环境信息。在发生Crash时会随着异常信息一起上报并在页面展示。
     * @param key key
     * @param value value
     */
    public static void setTagData(String key,String value){
        if("stack".equals(key)){
            Sentry.configureScope(scope -> {
                scope.setContexts(key, value);
            });
        }else {
            Sentry.configureScope(scope -> {
                scope.setTag(key, value);
            });
        }
    }

    public static void testMsg(String msg){
        SentryUtil.report(msg);
    }

    public static void testException(){

        try{
            int i = 1/0;
        }catch (Throwable throwable){
            SentryUtil.report(throwable);
        }
    }



}
