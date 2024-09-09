package com.hss01248.sentry.trace;

import com.hss01248.sentry.api.ITrace;
import com.hss01248.sentry.api.TraceInfo;

import java.util.Map;

import io.sentry.ITransaction;
import io.sentry.Sentry;

/**
 * @Despciption todo
 * @Author hss
 * @Date 9/9/24 2:57 PM
 * @Version 1.0
 */
public class SentryTraceImpl implements ITrace ,TraceInfo.ITraceReport{
    @Override
    public void start(TraceInfo traceInfo) {
        // A good name for the transaction is key, to help identify what this is about
        ITransaction transaction = Sentry.startTransaction(traceInfo.name, "task");
        //transaction.setTag();
        if(traceInfo.attributes !=null && !traceInfo.attributes.isEmpty()){
            for (String s : traceInfo.attributes.keySet()) {
                transaction.setData(s,traceInfo.attributes.get(s)+"");
            }
        }

        traceInfo.realTraceObj = transaction;
    }

    @Override
    public void stop(TraceInfo traceInfo) {
        ITransaction transaction = (ITransaction) traceInfo.realTraceObj;
        transaction.finish();
    }

    @Override
    public void doReport(TraceInfo traceInfo) {
        ITransaction transaction = Sentry.startTransaction(traceInfo.name, "task");
        for (Map.Entry<String, Long> stringLongEntry : traceInfo.metrics.entrySet()) {
            //transaction.set(stringLongEntry.getKey(),stringLongEntry.getValue());
        }
        for (Map.Entry<String, String> stringObjectEntry : traceInfo.attributes.entrySet()) {
            transaction.setData(stringObjectEntry.getKey(),stringObjectEntry.getValue()+"");
        }
        if(traceInfo.mainMetricDivideBy10){
            TraceHandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    transaction.finish();
                }
            },traceInfo.mainMetric/10);
        }else {
            TraceHandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    transaction.finish();
                }
            },traceInfo.mainMetric);
        }
    }
}
