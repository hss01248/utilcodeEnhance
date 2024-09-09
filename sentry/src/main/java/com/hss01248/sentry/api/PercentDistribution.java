package com.hss01248.sentry.api;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 22/04/2022 09:34
 * @Version 1.0
 */
public class PercentDistribution {

    public Map<String,String> attrs = new HashMap<>();

    protected PercentDistribution(String name) {
        this.name = name;
    }

    public String name;

    public static PercentDistribution create(String name){
        return new PercentDistribution(name);
    }
    public PercentDistribution addAttribute(String key,String value) {
        this.attrs.put(key, value);
        return this;
    }

    public void report(){
        if(ReporterContainer.shouldNotReport(this)){
            return;
        }
        if(ReporterContainer.percentReporter != null){
            ReporterContainer.percentReporter.doReport(this);
        }else {
            Log.v("percent","ReporterContainer.percentReporter not set");
        }
    }
}
