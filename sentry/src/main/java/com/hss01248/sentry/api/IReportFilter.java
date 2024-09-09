package com.hss01248.sentry.api;

public interface IReportFilter {

    /**
     * 作为切换统一拦截biz,trace,exception
     * @param object 可能为BizAbnormal,TraceInfo,Throwable
     * @return
     */
    boolean shouldNotReport(Object object);
}
