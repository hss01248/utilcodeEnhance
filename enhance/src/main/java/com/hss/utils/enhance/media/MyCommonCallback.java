package com.hss.utils.enhance.media;

import androidx.annotation.Nullable;

public interface MyCommonCallback<T> {
    void onSuccess(T t);

    void onError(String code, String msg,@Nullable Throwable throwable);
}
