package com.hss01248.sentry.api;

import com.blankj.utilcode.util.GsonUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/04/2022 16:48
 * @Version 1.0
 */
public class ReporterFilterHelper {

    public static Set<FilterConfig.ExceptionFilterInfo> exceptionFilters = new HashSet<>();
    public static Set<FilterConfig.BizFilterInfo> bizFilters = new HashSet<>();
    public static Set<FilterConfig.TraceFilterInfo> traceFilters = new HashSet<>();


    public static void addExceptionFilter(FilterConfig.ExceptionFilterInfo info){
        if(info != null){
            exceptionFilters.add(info);
        }
    }

    public static void addBizFilter(FilterConfig.BizFilterInfo info){
        if(info != null){
            bizFilters.add(info);
        }
    }

    public static void addTraceFilter(FilterConfig.TraceFilterInfo info){
        if(info != null){
            traceFilters.add(info);
        }
    }

    public static void refreshConfig(String configStr){

        try{
            FilterConfig filterConfig = GsonUtils.fromJson(configStr, FilterConfig.class);
            if(filterConfig != null){
                if(filterConfig.exceptionFilters != null && !filterConfig.exceptionFilters.isEmpty()){
                    exceptionFilters.addAll(filterConfig.exceptionFilters);
                }
                if(filterConfig.bizFilters != null && !filterConfig.bizFilters.isEmpty()){
                    bizFilters.addAll(filterConfig.bizFilters);
                }
                if(filterConfig.traceFilters != null && !filterConfig.traceFilters.isEmpty()){
                    traceFilters.addAll(filterConfig.traceFilters);
                }
            }
        }catch (Throwable throwable){
            ReporterContainer.reportWithLog(throwable);
        }
    }
}
