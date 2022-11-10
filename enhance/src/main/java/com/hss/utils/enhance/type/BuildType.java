package com.hss.utils.enhance.type;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * by hss
 * data:2020-04-26
 * desc:
 */
public class BuildType {

    public static final String BUILDTYPE_DEBUG = "debug";
    /**
     * 给测试人员打的包,以及给外部演示打的包
     * debug关闭
     * 默认为测试环境
     * 大多数开发工具已集成,但默认关闭
     * debug toast关闭
     * xlog开启(写文件)
     */
    public static final String BUILDTYPE_COMMON = "common";
    /**
     * 正式发版的包
     */
    public static final String BUILDTYPE_RELEASE = "release";

    /**
     * 与正式发版的包唯一区别是debug开启,用于输出method trace,衡量方法性能
     */
    public static final String BUILDTYPE_RELEASE_TRACE = "tracerelease";
    /**
     * 正式发多渠道的包
     */
    public static final String BUILDTYPE_MULTICHANNEL = "multichannel";

    public static String getBuildType() {
        return currentBuildType;
    }

    static  String currentBuildType = BUILDTYPE_RELEASE;

    /**
     * 初始化时传入即可
     * @param app
     * @param buildType
     */
     static void init(Application app, String buildType) {

         if(TextUtils.isEmpty(buildType)){
             buildType =  getBuildType(app);
         }
         Log.v("classpath",currentBuildType+"->"+buildType);
         currentBuildType = buildType;

    }
    static void init(Application app){
        currentBuildType =  getBuildType(app);
    }

    /**
     * 无法反射那种manifest,application路径,defaultconfig三者完全不搭边的情况,其他任意两种都行
     * @param app
     * @return
     */
    private static String getBuildType(Application app) {
        List<String> maybeClassPaths = new ArrayList<>();
        String classPath = app.getPackageName()+".BuildConfig";
        maybeClassPaths.add(classPath);
        String name = app.getClass().getName();
        if(name.contains(".")){
            name = name.substring(0,name.lastIndexOf("."));
            maybeClassPaths.add(name+".BuildConfig");
            if(name.contains(".")){
                name = name.substring(0,name.lastIndexOf("."));
                maybeClassPaths.add(name+".BuildConfig");
                if(name.contains(".")){
                    name = name.substring(0,name.lastIndexOf("."));
                    maybeClassPaths.add(name+".BuildConfig");
                }
            }
        }
        for (String path : maybeClassPaths) {
            try {
              Class  clazz = Class.forName(path);
                Field application_id = clazz.getField("APPLICATION_ID");
                application_id.setAccessible(true);
                Object obj  = application_id.get(clazz);
                //判断是否同一个包名:
                if(!app.getPackageName().equals(obj)){
                    continue;
                }
                Field build_type = clazz.getField("BUILD_TYPE");
                build_type.setAccessible(true);
                String buildType  = (String) build_type.get(clazz);
                if(!TextUtils.isEmpty(buildType)){
                    Log.i("classpath","found buildtype:"+ buildType);
                    return buildType;
                }
            }catch (Throwable throwable){
               Log.v("classpath",path,throwable);
                continue;
            }
        }
        Log.e("classpath","buildtype not found by reflect,please set manually!!");
        return BUILDTYPE_RELEASE;
    }

    public static boolean isDebug(){
        return currentBuildType.contains(BUILDTYPE_DEBUG);
    }
    public static boolean isCommon(){
        return currentBuildType.contains(BUILDTYPE_COMMON);
    }
    public static boolean isRelease(){
        return BUILDTYPE_RELEASE.equalsIgnoreCase(currentBuildType);
    }
    public static boolean isReleaseTrace(){
        return BUILDTYPE_RELEASE_TRACE.equalsIgnoreCase(currentBuildType);
    }
    public static boolean isMultichannel(){
        return BUILDTYPE_MULTICHANNEL.equalsIgnoreCase(currentBuildType);
    }





}
