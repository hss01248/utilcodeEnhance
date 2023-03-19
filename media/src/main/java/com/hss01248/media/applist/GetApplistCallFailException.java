package com.hss01248.media.applist;

public class GetApplistCallFailException extends Exception{
    public GetApplistCallFailException(String message) {
        super(message);
    }

    public GetApplistCallFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetApplistCallFailException(Throwable cause) {
        super(cause);
    }
}
