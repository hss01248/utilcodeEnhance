# sentry 

> 代码异常,业务异常上报

## gradle引用

```groovy
implementation 'com.github.hss01248.utilcodeEnhance:sentry:1.3.5'
```



# 初始化

manifest里配置即可

```xml
 <!-- Required: set your sentry.io project identifier (DSN) -->
        <meta-data tools:replace="android:value" android:name="io.sentry.dsn" android:value="https://xxxx@yyyy.ingest.sentry.io/67777" />
        <meta-data android:name="io.sentry.debug" android:value="true" />
        <!-- enable automatic breadcrumbs for user interactions (clicks, swipes, scrolls) -->
        <meta-data android:name="io.sentry.traces.user-interaction.enable" android:value="true" />
        <!-- enable screenshot for crashes -->
        <meta-data android:name="io.sentry.attach-screenshot" android:value="true" />
        <!-- enable view hierarchy for crashes -->
        <meta-data android:name="io.sentry.attach-view-hierarchy" android:value="true" />

        <!-- enable the performance API by setting a sample-rate, adjust in production env -->
        <meta-data android:name="io.sentry.traces.sample-rate" android:value="1.0" />
        <!-- enable profiling when starting transactions, adjust in production env -->
        <meta-data android:name="io.sentry.traces.profiling.sample-rate" android:value="1.0" />
```



# 使用

```java
 //普通msg
SentryUtil.report(msg);

//exception
try{
            int i = 1/0;
        }catch (Throwable throwable){
            SentryUtil.report(throwable);
        }
```

也可以额外加很多信息:

![image-20231017111839835](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20231017111839835.png)

通常,可以用于日志工具aop切入,一边打日志,超过info的日志,就上报到sentry.

比如:

```java
@Aspect
public class AspectLogUtils {

    /**
     * log(final int type, final String tag, final Object... contents)
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Before("execution(* com.blankj.utilcode.util.LogUtils.log(..))")
    public void weaveJoinPoint(JoinPoint joinPoint) throws Throwable {
        int type = (int) joinPoint.getArgs()[0];
        if(type < LogUtils.W){
            return;
        }
        Object[] objects= (Object[]) joinPoint.getArgs()[2];
        if(objects  == null || objects.length ==0){
            return;
        }
        SentryUtil.Builder builder = SentryUtil.create();
        boolean hasThrowable = false;
        int count = 0;
        for (Object object : objects) {
            if(object instanceof  Throwable){
                hasThrowable = true;
                Throwable throwable = (Throwable) object;
                builder.exception(throwable);
            }else {
                count++;
                builder.addExtra("extra"+count,object+"");
            }
        }
        String typeStr = "warn";
        if(type == LogUtils.E){
            typeStr = "error";
        }else if(type == LogUtils.A){
            typeStr = "assert";
        }else {
            typeStr = type+"";
        }
        builder.addTag("logLevel",typeStr);
        if(hasThrowable){
           // builder.addExtra("extraMsg",joinPoint.getArgs()[0]+"");
        }else {
            builder.msg(objects[0]+"");
        }
        String tag = joinPoint.getArgs()[1]+"";
        if(!tag.equals("") && !"null".equals(tag)){
            builder.addTag("extraTag",joinPoint.getArgs()[1]+"");
        }
        builder.doReport();
    }
}
```



# 官方控制台:

https://sentry.io

![image-20231017112350460](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20231017112350460.png)

![image-20231017112413484](https://cdn.jsdelivr.net/gh/shuiniuhss/myimages@main/imagemac3/image-20231017112413484.png)



# 初始化耗时优化:

com.hss01248.sentry.SentryUtil.init()   cost(ms)====>10450