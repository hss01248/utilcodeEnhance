package com.hss01248.sentry.api;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 04/03/2022 17:14
 * @Version 1.0
 */
public class TraceInfo {

    private TraceInfo(String name) {
        this.name = name;
    }

    public String name;

    public void setRealTraceObj(Object realTraceObj) {
        this.realTraceObj = realTraceObj;
    }

    public Object realTraceObj;

    public boolean mainMetricDivideBy10 = false;

    /**
     *  time/10,避免延迟xxs导致的丢失
     * @param mainMetricDivideBy10
     * @return
     */
    public TraceInfo setMainMetricDivideBy10(boolean mainMetricDivideBy10) {
        this.mainMetricDivideBy10 = mainMetricDivideBy10;
        return this;
    }

    public TraceInfo setMainMetric(long mainMetric) {
        this.mainMetric = mainMetric;
        return this;
    }
    public TraceInfo onlyForDebug(boolean onlyForDebug) {
        this.onlyForDebug = onlyForDebug;
        return this;
    }
    public boolean onlyForDebug;
    public long mainMetric;

    public Map<String,Long> metrics = new HashMap<>();
    public Map<String,String> attributes = new HashMap<>();

    public static TraceInfo create(String name) {
        return new TraceInfo(name);
    }

    public TraceInfo addAttribute(String key,String value) {
        this.attributes.put(key, value);
        return this;
    }

    public void report(){
        if(this.onlyForDebug){
            if(!ReporterContainer.isDebug()){
                return;
            }
        }
        if(ReporterContainer.shouldNotReport(this)){
            return;
        }
        if(ReporterContainer.getTraceReporter() != null){
            addMetirc("00_"+name,mainMetric);
            if(ReporterContainer.isDebug()){
                Log.v("trace",this.toString());
            }
            ReporterContainer.getTraceReporter().doReport(this);
        }else {
            if(ReporterContainer.isDebug()){
                Log.w("trace","report not set to TracerWrapper");
            }

        }

    }
    /**
     * firebase metric, 最多32个
     * @param name
     * @param metric
     * @return
     */
    public TraceInfo addMetirc(String name,long metric) {
        metrics.put(name,metric);
        return this;
    }

    public interface ITraceReport{
        void  doReport(TraceInfo info);
    }

    @Override
    public String toString() {
        return "TraceInfo{" +
                "name='" + name + '\'' +
                ", realTraceObj=" + realTraceObj +
                ", onlyForDebug=" + onlyForDebug +
                ", mainMetric=" + mainMetric +
                ", metrics=" + metrics +
                ", attributes=" + attributes +
                '}';
    }
}
