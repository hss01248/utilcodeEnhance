package com.hss01248.sentry.api;

public interface ITrace {

    void start(TraceInfo traceInfo);

    void stop(TraceInfo traceInfo);
}
