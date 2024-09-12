package com.hss01248.sentry.api;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 用于总量计数,以及计算百分比分布
 * msg -> 对应sentry的msg,或者firebase的trace的名字
 * tag里的key-val  对应sentry的tag,或者firebase的的attribute,
 */
public class BizAbnormal {

    String businessName;

    private BizAbnormal(String msg) {
        this.msg = msg;
    }





    public String getBusinessName() {
        return businessName;
    }

    public String getMsg() {
        return msg;
    }

    public int getLevel() {
        return level;
    }

    public BizAbnormal onlyForDebug(boolean onlyForDebug) {
        this.onlyForDebug = onlyForDebug;
        return this;
    }
    public boolean onlyForDebug;

    String msg;
    int level = Log.INFO ;

    public Map<String, Object> getExtras() {
        return extras;
    }

    /**
     * 按插入顺序来排序
     */
    Map<String, Object> extras = new LinkedHashMap<>();

    public Map<String, String> getTags() {
        return tags;
    }

    Map<String, String> tags = new LinkedHashMap<>();

    public BizAbnormal businessTypeName(String businessName) {
        this.businessName = businessName;
        return this;
    }



    /**
     * 主信息.在sentry是msg, 在firebase是trace的name, exception的name
     * @param msg
     * @return
     */
    public BizAbnormal msg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     *
     * @param logLevel 取值Android.util.Log中的几个值
     * @return
     */
    public BizAbnormal level(String logLevel) {
        this.level = level;
        return this;
    }

    /**
     * 添加额外的信息,已丰富msg的背景信息
     * @param key
     * @param value
     * @return
     */
    public BizAbnormal addExtra(String key, Object value) {
        this.extras.put(key, value);
        return this;
    }

    /**
     * 添加额外的tag,用作过滤
     * @param key
     * @param value
     * @return
     */
    public BizAbnormal addTag(String key, String value) {
        this.tags.put(key, value);
        return this;
    }

    public static BizAbnormal create(String nameOrMsg) {
        return new BizAbnormal(nameOrMsg);
    }

    public void report(){
        if(this.onlyForDebug){
            if(!ReporterContainer.isDebug()){
                //Log.v("biz","biz reporter not set");
                return;
            }
        }
        if(ReporterContainer.shouldNotReport(this)){
            return;
        }
        if(ReporterContainer.getBizReporter() != null){
            ReporterContainer.getBizReporter().doReport(this);
        }else {
            Log.w("biz","biz reporter not set");
        }
        if(ReporterContainer.isDebug()){
            Log.i("biz","业务异常上报: "+ this.toString());
        }

    }

    @Override
    public String toString() {
        return "BizAbnormal{" +
                "businessName='" + businessName + '\'' +
                ", onlyForDebug=" + onlyForDebug +
                ", msg='" + msg + '\'' +
                ", level=" + levelDesc(level) +
                ", extras=" + extras +
                ", tags=" + tags +
                '}';
    }

    private String levelDesc(int level) {
        switch (level){
            case Log.INFO:
                return "info";
            case Log.WARN:
                return "warn";
            case Log.ERROR:
                return "error";
            case Log.DEBUG:
                return "debug";
            case Log.VERBOSE:
                return "verbose";
            case Log.ASSERT:
                return "assert";
        }
        return "unknown";
    }

    /**
     sentry的实现
     EventBuilder event = new EventBuilder();
     event.withMessage(msg)
     .withTag("business",businessName)
     .withTag("isBusiness","true")
     .withLevel(SentryUtil.transLevel(level));



     //event.setEnvironment("rn");
     // event.setTag("platform","rn");


     //额外的tag,不能直接设,会覆盖globaltag
     if(tags != null && !tags.isEmpty()){
     Set<Map.Entry<String,String>> entry = tags.entrySet();
     for (Map.Entry<String, String> en : entry) {
     event.withTag(en.getKey(),en.getValue());
     }
     }

     if(extras != null && !extras.isEmpty()){
     Set<Map.Entry<String,Object>> entry = extras.entrySet();
     for (Map.Entry<String, Object> en : entry) {
     event.withExtra(en.getKey(),en.getValue());
     }
     }

     if(SentryUtil.getNativeClient() != null){
     SentryUtil.getNativeClient().sendEvent(event);
     } */
    public interface IBizReport{
       void  doReport(BizAbnormal abnormal);
    }


}
