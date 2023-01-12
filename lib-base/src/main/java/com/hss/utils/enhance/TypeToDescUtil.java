package com.hss.utils.enhance;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @Despciption todo
 * @Author hss
 * @Date 12/01/2023 10:00
 * @Version 1.0
 */
public class TypeToDescUtil {

    public static String getDescByType(Object type,Class clazz){
        if(type == null){
            return "null";
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        if(declaredFields == null || declaredFields.length ==0){
            return "";
        }
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if(Modifier.isFinal(declaredField.getModifiers()) && Modifier.isStatic(declaredField.getModifiers())){
                try {
                   Object obj =  declaredField.get(clazz);
                   if(obj == type){
                       String name = declaredField.getName();
                       if(!TextUtils.isEmpty(name)){
                           return name.toLowerCase();
                       }else {
                           LogUtils.w("declaredField.getName() is empty: ",declaredField);
                       }
                   }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return "unfound";
    }
}
