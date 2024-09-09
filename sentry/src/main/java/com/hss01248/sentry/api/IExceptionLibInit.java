package com.hss01248.sentry.api;

import java.util.Map;

public interface IExceptionLibInit {

    /**
     *
     * context : 两者都有
     * bugly:
    private String appId; 注册时申请的APPID
    private String channel; 渠道
    private String userId;用户id

     sentry:
    private String dsn; //dsn服务器
     * @return
     */
    Map<String,Object> getInitParam();
}
