package com.hss01248.ireporter;

/**
 * @Despciption todo
 * @Author hss
 * @Date 23/11/2022 14:05
 * @Version 1.0
 */
public class Reporter {

    public static void setReporter(IReporter reporter) {
        Reporter.reporter = reporter;
    }

    private static IReporter reporter;

    public static void report(Throwable throwable){
        if(reporter != null){
            reporter.reportException(throwable);
        }
    }

    public static  void reportMsg(String type,String msg){
        if(reporter != null){
            reporter.reportMsg(type, msg);
        }
    }
}
