package com.hss01248.ireporter;

public interface IReporter {


    void reportException(Throwable throwable);

    void reportMsg(String type,String msg);
}
