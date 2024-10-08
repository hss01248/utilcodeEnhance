package com.hss01248.sentry;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.MeasurementUnit;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SpanStatus;
import io.sentry.protocol.Message;
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

        try{
            Sentry.captureEvent(buildEventBuilder());
        }catch (Throwable throwable){
            if(isDebug){
                throwable.printStackTrace();
            }
        }

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
        buildCommon(eventBuilder);

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

    private void buildCommon(SentryEvent eventBuilder) {
        eventBuilder.setTag("manufacturer",Build.MANUFACTURER);
        eventBuilder.setTag("brand",Build.BRAND);
        eventBuilder.setTag("model",Build.MODEL);
        eventBuilder.setTag("isBackground", !AppUtils.isAppForeground()+"");
        eventBuilder.setTag("network", network());
        if(SentryUtil.getIGetInfo() != null){
            eventBuilder.setTag("topPage", SentryUtil.getIGetInfo().topPageName());
            boolean unlogin = IGetInfo.UID_NOT_LOGIN.equals(SentryUtil.getIGetInfo().uid())
                    || "".equals(SentryUtil.getIGetInfo().uid());
            if(unlogin){
                eventBuilder.setTag("uid", "unlogin");
            }else {
                eventBuilder.setTag("uid", SentryUtil.getIGetInfo().uid());
            }

            eventBuilder.setModule("pageStack", SentryUtil.getIGetInfo().currentPageStack());

            eventBuilder.setTag("account", SentryUtil.getIGetInfo().account());
            String uid = SentryUtil.getIGetInfo().uid();
            if(!IGetInfo.NOT_SET.equals(uid) && !unlogin){
                User user = new User();
                user.setId("unlogin");
                user.setUsername(SentryUtil.getIGetInfo().account());
                eventBuilder.setUser(user);
            }
            eventBuilder.setTag("deviceId", SentryUtil.getIGetInfo().deviceId());
        }
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
            //缩减上报数据大小
            Sentry.clearBreadcrumbs();
            if (o instanceof SentryEvent) {
                Sentry.captureEvent((SentryEvent) o);
            }  else if(o instanceof String){
                SentryUtil.create()
                        .msg((String) o)
                        .doReport();
            }else if(o instanceof Throwable){
                SentryUtil.create()
                        .exception((Throwable) o)
                        .doReport();
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

    public static void testMetrics1(){
        final ISpan span = Sentry.getSpan();
        if (span != null) {
            // Record amount of memory used
            span.setMeasurement("memory_used", 64, MeasurementUnit.Information.MEGABYTE);

            // Record time it took to load user profile
            //span.setMeasurement("user_profile_loading_time", 1.3, MeasurementUnit.Duration.SECOND);

            // Record number of times the screen was loaded
            //span.setMeasurement("screen_load_count", 4);
            span.finish();
        }else{
            LogUtils.w("span == null");
        }
    }

    public static void testMetrics2(){
        final ISpan span = Sentry.getSpan();
        if (span != null) {
            // Record amount of memory used
            span.setMeasurement("memory_used", 64, MeasurementUnit.Information.MEGABYTE);

            // Record time it took to load user profile
            span.setMeasurement("user_profile_loading_time", 1.3, MeasurementUnit.Duration.SECOND);

            // Record number of times the screen was loaded
            //span.setMeasurement("screen_load_count", 4);
            span.finish();
        }else{
            LogUtils.w("span == null");
        }
    }

    public static void testInstrumentation(){
        ITransaction transaction = Sentry.startTransaction("processOrderBatch()", "task");
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
        } finally {
            transaction.finish();
        }
    }



}
