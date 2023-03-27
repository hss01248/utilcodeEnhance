package com.hss01248.utils.ext;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @Despciption 不得直接使用GsonUtils
 * @Author hss
 * @Date 27/03/2023 19:38
 * @Version 1.0
 */
public class MyJson {


    public static String toStr(Object obj){
        return GsonUtils.toJson(obj);
    }

    /**
     * 解析容器类泛型
     * GsonUtils.fromJson(json,new TypeToken<List<String>>(){}.getType())
     * @param json
     * @param type
     * @return
     * @param <T>
     */
    public static <T> T fromJson(String json, Type type){
        try {
            return GsonUtils.fromJson(json,type);
        }catch (Throwable throwable){
            if(AppUtils.isAppDebug()){
                throw new RuntimeException(throwable);
            }
            LogUtils.w(throwable);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> tClass){
        try {
            return GsonUtils.fromJson(json,tClass);
        }catch (Throwable throwable){
            if(AppUtils.isAppDebug()){
                throw new RuntimeException(throwable);
            }
            LogUtils.w(throwable);
            return null;
        }
    }
}
