package com.hss01248.sentry.api;

import androidx.annotation.Keep;

import java.util.HashSet;
import java.util.Set;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/04/2022 16:32
 * @Version 1.0
 示例:
{
"exceptionFilters": [
{
"msgContains": "",
"className": "ConfigRequestFailedException"
}
],
"bizFilters": [
{
"bizName": "下单",
"message": "risk check failed"
}
],
"traceFilters": [
"apm_",
"method_xlog_2"
]
}
 */
@Keep
public class FilterConfig {

    public Set<ExceptionFilterInfo> exceptionFilters = new HashSet<>();
    public Set<BizFilterInfo> bizFilters = new HashSet<>();
    public Set<TraceFilterInfo> traceFilters = new HashSet<>();



    @Keep
    public static class TraceFilterInfo{
        public String nameContains;
        public String attrContains;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TraceFilterInfo)) return false;

            TraceFilterInfo that = (TraceFilterInfo) o;

            if (nameContains != null ? !nameContains.equals(that.nameContains) : that.nameContains != null)
                return false;
            return attrContains != null ? attrContains.equals(that.attrContains) : that.attrContains == null;
        }

        @Override
        public int hashCode() {
            int result = nameContains != null ? nameContains.hashCode() : 0;
            result = 31 * result + (attrContains != null ? attrContains.hashCode() : 0);
            return result;
        }
    }


    @Keep
    public static class ExceptionFilterInfo {
        public String msgContains;
        public String className;
        public ExceptionFilterInfo() {

        }
        public ExceptionFilterInfo(String msgContains, String className) {
            this.msgContains = msgContains;
            this.className = className;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExceptionFilterInfo)) return false;

            ExceptionFilterInfo that = (ExceptionFilterInfo) o;

            if (msgContains != null ? !msgContains.equals(that.msgContains) : that.msgContains != null)
                return false;
            return className != null ? className.equals(that.className) : that.className == null;
        }

        @Override
        public int hashCode() {
            int result = msgContains != null ? msgContains.hashCode() : 0;
            result = 31 * result + (className != null ? className.hashCode() : 0);
            return result;
        }
    }

    @Keep
    public static class BizFilterInfo {
        public String bizNameContains;
        public String messageContains;

        public BizFilterInfo() {
        }
        public BizFilterInfo(String bizNameContains, String messageContains) {
            this.bizNameContains = bizNameContains;
            this.messageContains = messageContains;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BizFilterInfo)) return false;

            BizFilterInfo that = (BizFilterInfo) o;

            if (bizNameContains != null ? !bizNameContains.equals(that.bizNameContains) : that.bizNameContains != null)
                return false;
            return messageContains != null ? messageContains.equals(that.messageContains) : that.messageContains == null;
        }

        @Override
        public int hashCode() {
            int result = bizNameContains != null ? bizNameContains.hashCode() : 0;
            result = 31 * result + (messageContains != null ? messageContains.hashCode() : 0);
            return result;
        }
    }
}
