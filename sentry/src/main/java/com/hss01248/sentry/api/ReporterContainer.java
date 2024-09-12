package com.hss01248.sentry.api;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Despciption todo
 * @Author hss
 * @Date 01/04/2022 16:13
 * @Version 1.0
 */
public class ReporterContainer {

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        ReporterContainer.debug = debug;
    }


    public static IGetInfo getIGetInfo() {
        return getInfo;
    }

    public static void setIGetInfo(IGetInfo getInfo) {
        ReporterContainer.getInfo = getInfo;
    }

    static  IGetInfo getInfo = new IGetInfo() {
        @Override
        public String uid() {
            return IGetInfo.NOT_SET;
        }
    };
    /**
     *  //子线程+子进程调用,会发生UtilActivityLifecyleImpl初始化失败,类异常,即使try住了,也会导致下次再调用到UtilActivityLifecyleImpl是抛出NoClassDefFoundError.
     *  根本原因是UtilActivityLifecyleImpl里new了一个activity.而子线程不能创建handler,如果没有调用Looper.prepare().
     * @return
     */
   public static boolean getDebugWhenInit(){
        try {
            if("main".equals(Thread.currentThread().getName())){
                return AppUtils.isAppDebug();
            }

            return false;

        }catch (Throwable throwable){
            throwable.printStackTrace();
            return false;
        }
    }

    //todo 启动
    static boolean debug = getDebugWhenInit();

    static IReportFilter reportFilter = new DefaultReportFilter();

    static TraceInfo.ITraceReport traceReporter;

    static BizAbnormal.IBizReport bizReporter;

    public static void setBeforeExceptionReport(IBeforeExceptionReport beforeExceptionReport) {
        ReporterContainer.beforeExceptionReport = beforeExceptionReport;
    }

    static IBeforeExceptionReport beforeExceptionReport;

    public static ReportInterceptor getReportInterceptor() {
        return reportInterceptor;
    }

    public static void setReportInterceptor(ReportInterceptor reportInterceptor) {
        ReporterContainer.reportInterceptor = reportInterceptor;
    }

    static ReportInterceptor reportInterceptor;

    public static void setPercentReporter(IPercentReporter percentReporter) {
        ReporterContainer.percentReporter = percentReporter;
    }

    static IPercentReporter percentReporter;


    public static void addTagSetter(IGlobalTagSetter tagSetter) {
        ReporterContainer.tagSetters.add(tagSetter);
    }

    static List<IGlobalTagSetter> tagSetters = new ArrayList<>();

    static List<IExceptionReporter> exceptionReporters = new ArrayList<>();

    public static IReportFilter getReportFilter() {
        return reportFilter;
    }

    public static void setReportFilter(IReportFilter reportFilter) {
        ReporterContainer.reportFilter = reportFilter;
    }

    public static TraceInfo.ITraceReport getTraceReporter() {
        return traceReporter;
    }

    public static void setTraceReporter(TraceInfo.ITraceReport traceReporter) {
        ReporterContainer.traceReporter = traceReporter;
    }

    public static void setGlobalTag(Context context,String key,String val){
        for (IGlobalTagSetter tagSetter : tagSetters) {
            try {
                tagSetter.setTagData(context, key, val);
            }catch (Throwable throwable){
                if(ReporterContainer.isDebug()){
                    throwable.printStackTrace();
                }
                ReporterContainer.report(throwable);
            }

        }
    }



    public static BizAbnormal.IBizReport getBizReporter() {
        return bizReporter;
    }

    public static void setBizReporter(BizAbnormal.IBizReport bizReporter) {
        ReporterContainer.bizReporter = bizReporter;
    }



    public static void addExceptionReporters(IExceptionReporter exceptionReporter) {
        ReporterContainer.exceptionReporters.add(exceptionReporter);
    }


    public static boolean shouldNotReport(Object object){
        if(reportInterceptor != null){
            try {
                object = reportInterceptor.interceptor(object);
            }catch (Throwable throwable){
                if(isDebug()){
                    throwable.printStackTrace();
                }
            }
        }
        if(object == null){
            Log.v("reportFilter","object is null after reportInterceptor");
            return false;
        }
        if(reportFilter != null){
            try {
                if(reportFilter.shouldNotReport(object)){
                    if(ReporterContainer.isDebug()){
                        Log.v("reportFilter","ignored by getReportFilter().shouldNotReport():"+ object);
                    }
                    return true;
                }else {
                    return false;
                }
            }catch (Throwable throwable){
                if(isDebug()){
                    throwable.printStackTrace();
                }
                return false;
            }
        }else {
            return false;
        }
    }

    public static void reportWithLog(Throwable e){
        if(isDebug()){
            e.printStackTrace();
        }
        report(e);
    }
    public static void report(Throwable e) {
        if(beforeExceptionReport != null){
            beforeExceptionReport.beforeReport("",e);
        }
        if(ReporterContainer.shouldNotReport(e)){
            return;
        }

        List<IExceptionReporter> iReportException = exceptionReporters;
        if(iReportException != null && !iReportException.isEmpty()){
            ThreadPoolManager.runOnBack(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < iReportException.size(); i++) {
                        if(iReportException.get(i) != null){
                            try {
                                iReportException.get(i).report(e);
                            }catch (Throwable throwable){
                                if(isDebug()){
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });

        }
    }


    public static void report(String tag, Throwable e) {
        if(beforeExceptionReport != null){
            beforeExceptionReport.beforeReport(tag,e);
        }
        if(ReporterContainer.shouldNotReport(e)){
            return;
        }
        List<IExceptionReporter> iReportException = exceptionReporters;
        if(iReportException != null && !iReportException.isEmpty()){
            ThreadPoolManager.runOnBack(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < iReportException.size(); i++) {
                        if(iReportException.get(i) != null){
                            try {
                                iReportException.get(i).report(e,tag);
                            }catch (Throwable throwable){
                                if(isDebug()){
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });

        }
    }


    public static void report(Throwable e, Object... objects) {
        if(beforeExceptionReport != null){
            beforeExceptionReport.beforeReport("",e);
        }
        if(ReporterContainer.shouldNotReport(e)){
            return;
        }
        List<IExceptionReporter> iReportException = exceptionReporters;
        if(iReportException != null && !iReportException.isEmpty()){
            ThreadPoolManager.runOnBack(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < iReportException.size(); i++) {
                        if(iReportException.get(i) != null){
                            try {
                                iReportException.get(i).report(e,objects);
                            }catch (Throwable throwable){
                                if(isDebug()){
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });

        }
    }


    public static void reportExceptionWithTagOrAttributs(Throwable e, Map<String, String> tagsOrAttributes) {
        if(beforeExceptionReport != null){
            beforeExceptionReport.beforeReport("",e);
        }
        if(ReporterContainer.shouldNotReport(e)){
            return;
        }
        List<IExceptionReporter> iReportException = exceptionReporters;
        if(iReportException != null && !iReportException.isEmpty()){
            ThreadPoolManager.runOnBack(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < iReportException.size(); i++) {
                        if(iReportException.get(i) != null){
                            try {
                                iReportException.get(i).reportExceptionWithTagOrAttributs(e,tagsOrAttributes);
                            }catch (Throwable throwable){
                                if(isDebug()){
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });

        }else {
            //wTag("trace","iReportException not set,please invode XlogUtil2.setExceptionReport()");
        }
    }


    /**
     * instanceof只能判断一层.和getInterfaces一个尿性
     * @param obj
     * @return
     */
    public static boolean isInstanceOfThrowable(Object obj) {
        if(obj instanceof String){
            return false;
        }
        if(obj instanceof Exception ){
            return true;
        }
        if(obj instanceof Throwable){
            return true;
        }
        Class[] classes = getInterfaces2(obj);
        if(classes == null || classes.length == 0){
            return false;
        }
        for (Class aClass : classes) {
            if(Throwable.class.equals(aClass)){
                return true;
            }
        }
        return false;
    }

    private static <T> Class<?>[] getInterfaces2(T impl) {
        Set<Class> classes = getInterfaces(impl.getClass(),null);
        if(classes.size() > 0){
            Class[] classes1 = new Class[classes.size()];
            Iterator<Class> iterator = classes.iterator();
            int i = 0;
            while (iterator.hasNext()){
                classes1[i] = iterator.next();
                i++;
            }
            return classes1;
        }
        return null;
    }

    private static <T> Set<Class> getInterfaces(Class clazz, Set<Class> classes) {
        if(classes == null){
            classes = new HashSet<>();
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        if(interfaces == null || interfaces.length == 0){

        }else {
            for (Class<?> anInterface : interfaces) {
                classes.add(anInterface);
                //接口的父类
                Class superInter = anInterface.getSuperclass();
                if(superInter != null){
                    classes.addAll(getInterfaces(superInter,classes));
                }
            }
        }
        //类的父类
        Class superClazz = clazz.getSuperclass();
        if(superClazz != null){
            classes.addAll(getInterfaces(superClazz,classes));
        }
        return classes;
    }
}
