package com.hss01248.sentry.api;


import android.content.Context;

public interface IGlobalTagSetter {


    void setTagData(Context context,String key, String value);
}
