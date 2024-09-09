package com.hss01248.sentry.api;

import android.text.TextUtils;

import java.util.Map;
import java.util.TreeMap;

public interface IExceptionReporter {

    default void report(Throwable throwable){
        Map<String, String> tagsOrAttributes = new TreeMap<>();

        Throwable throwable2 = findRoot(throwable);
        if(throwable2 != null && !TextUtils.isEmpty(throwable2.getMessage())){
            tagsOrAttributes.put("excetionTag",throwable2.getMessage());
        }
        report(throwable,tagsOrAttributes);
    }

    default void report(String tag,Throwable throwable){
        Map<String, String> tagsOrAttributes = new TreeMap<>();
        tagsOrAttributes.put("excetionTag",tag);
        report(throwable,tagsOrAttributes);
    }

    default void report(Throwable throwable,Object... objects){
        //report(throwable);
        Map<String, String> tagsOrAttributes = new TreeMap<>();
        if(objects != null && objects.length>0){
            int i = 0;
            for (Object object : objects) {
                i++;
                tagsOrAttributes.put("excetionTag"+i,object+"");
            }
        }
        reportExceptionWithTagOrAttributs(throwable,tagsOrAttributes);
    }

     void reportExceptionWithTagOrAttributs(Throwable throwable, Map<String,String> tagsOrAttributes);

    default Throwable findRoot(Throwable throwable) {
        if(throwable ==null){
            return throwable;
        }
        while (throwable.getCause() != null){
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
