package com.hss.utils.base.api;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

public interface MyCommonCallback3<T> {
    void onSuccess(T t);


    default void onError( String msg){
        onError("-1",msg,null);
    }

   default void onError(String code, String msg,@Nullable Throwable throwable){
       LogUtils.w(code,msg,throwable);
   }
}
