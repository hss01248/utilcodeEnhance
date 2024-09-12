package com.hss01248.sentry.trace;


import android.content.Context;

import androidx.annotation.Keep;
import androidx.startup.Initializer;

import com.hss01248.sentry.api.ReporterContainer;
import com.hss01248.sentry.api.TracerWrapper;

import java.util.ArrayList;
import java.util.List;


/**
 * @Despciption todo
 * @Author hss
 * @Date 17/02/2022 20:16
 * @Version 1.0
 */
@Keep
public class TraceImplInit implements Initializer<String> {
    @Override
    public String create(Context context) {
        init2();
        return "traceLogInit";
    }

    private void init2() {
        //就是简单
        TraceHandlerUtil.init();
        ReporterContainer.setTraceReporter(new SentryTraceImpl());
        TracerWrapper.setTraceImpl(new SentryTraceImpl());

       /* LogUtils.getConfig().setOnConsoleOutputListener(new LogUtils.OnConsoleOutputListener() {
            @Override
            public void onConsoleOutput(int type, String tag, String content) {

            }
        });*/
    }

    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
