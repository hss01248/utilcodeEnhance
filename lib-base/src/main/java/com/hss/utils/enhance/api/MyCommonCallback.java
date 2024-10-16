package com.hss.utils.enhance.api;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

/**
 * 请使用MyCommonCallback3
 * @param <T>
 */
@Deprecated
public interface MyCommonCallback<T> {
    void onSuccess(T t);


    default void onError( String msg){
        onError("-1",msg,null);
    }

   default void onError(String code, String msg,@Nullable Throwable throwable){
       LogUtils.w(code,msg,throwable);
   }

    default void onError( Throwable e){
        if(e ==null){
            onError("unknown error");
            return;
        }
        onError(e.getClass().getSimpleName(),e.getMessage(),e);
    }
}
