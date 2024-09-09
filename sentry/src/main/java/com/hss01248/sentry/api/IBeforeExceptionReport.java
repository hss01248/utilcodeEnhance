package com.hss01248.sentry.api;

public interface IBeforeExceptionReport {

    void beforeReport(String tag, Throwable e);
}
