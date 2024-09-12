package com.hss01248.sentry.api;
import android.util.Log;

/**
 * @Despciption todo
 * @Author hss
 * @Date 04/03/2022 17:34
 * @Version 1.0
 */
public class TracerWrapper {





    public static void setTraceImpl(ITrace iTrace) {
        TracerWrapper.iTrace = iTrace;
    }

    static ITrace iTrace;





    private TracerWrapper(String name) {
        this.info = TraceInfo.create(name);
    }

    TraceInfo info;
    public static TracerWrapper create(String name) {
        return new TracerWrapper(name);
    }

    public TracerWrapper addAttribute(String key,String value) {
        this.info.addAttribute(key, value);
        return this;
    }
    public TracerWrapper onlyForDebug(boolean onlyForDebug) {
        this.info.onlyForDebug(onlyForDebug);
        return this;
    }

    public TracerWrapper addMetirc(String name,long metric) {
        this.info.addMetirc(name,metric);
        return this;
    }

    long start;
    public TracerWrapper start(){
        start = System.currentTimeMillis();
        if(this.info.onlyForDebug){
            if(!ReporterContainer.isDebug()){
                return this;
            }
        }
        if(ReporterContainer.shouldNotReport(info)){
            return this;
        }
        if(iTrace != null){
            iTrace.start(info);
        }
        return this;
    }

    public void stop(){
        if(this.info.onlyForDebug){
            if(!ReporterContainer.isDebug()){
                return;
            }
        }
        if(ReporterContainer.shouldNotReport(info)){
            return;
        }

        if(iTrace != null){
            iTrace.stop(info);
            this.info.setMainMetric(System.currentTimeMillis() - start);
        }else {
            if(start != 0){
                this.info.setMainMetric(System.currentTimeMillis() - start);
                if(ReporterContainer.getTraceReporter() != null){
                    ReporterContainer.getTraceReporter().doReport(info);
                }
            }else {
                Log.w("trace","not called start() yet");
            }
        }
        if(ReporterContainer.isDebug()){
            Log.v("trace","性能监测上报: "+info.toString());
        }
    }



}
