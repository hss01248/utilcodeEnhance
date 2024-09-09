package com.hss01248.sentry.api;

import android.text.TextUtils;

import java.util.Set;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/04/2022 16:57
 * @Version 1.0
 */
public class DefaultReportFilter implements IReportFilter {
    @Override
    public boolean shouldNotReport(Object object) {
        if(object instanceof TraceInfo){
            TraceInfo info = (TraceInfo) object;
            if(!TextUtils.isEmpty(info.name)){
                Set<FilterConfig.TraceFilterInfo> traceFilters = ReporterFilterHelper.traceFilters;
                if(traceFilters == null || traceFilters.isEmpty()){
                    return false;
                }
                for (FilterConfig.TraceFilterInfo traceFilter : traceFilters) {
                    if(TextUtils.isEmpty(traceFilter.nameContains)){
                        if(!TextUtils.isEmpty(traceFilter.attrContains)){
                            for (String s : info.attributes.keySet()) {
                                if(s.contains(traceFilter.attrContains)){
                                    return true;
                                }
                            }
                        }
                    }else {
                        if(info.name.contains(traceFilter.nameContains)){
                            if(!TextUtils.isEmpty(traceFilter.attrContains)){
                                for (String s : info.attributes.keySet()) {
                                    if(s.contains(traceFilter.attrContains)){
                                        return true;
                                    }
                                }
                            }else {
                                return true;
                            }
                        }
                    }
                }
            }else {
                return true;
            }

        }else if(object instanceof BizAbnormal){
            BizAbnormal abnormal = (BizAbnormal) object;
            Set<FilterConfig.BizFilterInfo> beans2 = ReporterFilterHelper.bizFilters;
            if(beans2 == null || beans2.isEmpty()){
                return false;
            }
           return checkIfReportBiz(abnormal,beans2);

        }else {
            if(object instanceof  Throwable){
                return checkIfReport((Throwable)object);
            }else if(object instanceof String){
                String msg = (String) object;
                if(TextUtils.isEmpty(msg)){
                    return true;
                }
                Set<FilterConfig.ExceptionFilterInfo> beans2 = ReporterFilterHelper.exceptionFilters;
                if(beans2 == null || beans2.isEmpty()){
                    return false;
                }
                for (FilterConfig.ExceptionFilterInfo filterInfo : beans2) {
                    if(TextUtils.isEmpty(filterInfo.msgContains)){
                        continue;
                    }
                    if(msg.contains(filterInfo.msgContains)){
                        return true;
                    }
                }
            }

            //string, throwable

        }
        return false;
    }

    private boolean checkIfReportBiz(BizAbnormal abnormal, Set<FilterConfig.BizFilterInfo> beans2) {
        try {
            for (FilterConfig.BizFilterInfo info : beans2) {
                if(TextUtils.isEmpty(info.bizNameContains)){
                    if(!TextUtils.isEmpty(info.messageContains)){
                        if(!TextUtils.isEmpty(abnormal.getMsg())){
                            if(abnormal.getMsg().contains(info.messageContains)){
                                return true;
                            }
                        }
                    }
                }else {
                    if(!TextUtils.isEmpty(abnormal.getBusinessName())){
                        if(abnormal.getBusinessName().contains(info.bizNameContains)){
                            if(!TextUtils.isEmpty(info.messageContains)){
                                if(!TextUtils.isEmpty(abnormal.getMsg())){
                                    if( abnormal.getMsg().contains(info.messageContains)){
                                        return true;
                                    }
                                }
                            }else {
                                return true;
                            }
                        }
                    }
                }
            }
        }catch (Throwable throwable){
            if(ReporterContainer.isDebug()){
                throwable.printStackTrace();
            }

        }
        return false;
    }

    private boolean checkIfReport(Throwable exception) {
        try {
            String msg = exception.getMessage();
            if(!TextUtils.isEmpty(msg) && msg.contains("Receiver not registered: com.bc.sms.SmsRetrieverReceiver")){
                return true;
            }

            Set<FilterConfig.ExceptionFilterInfo> beans2 = ReporterFilterHelper.exceptionFilters;
            if(beans2 == null || beans2.isEmpty()){
                return false;
            }
            for (FilterConfig.ExceptionFilterInfo bean : beans2) {
                if(TextUtils.isEmpty(bean.className)){
                    if(!TextUtils.isEmpty(bean.msgContains) && !TextUtils.isEmpty(msg)){
                        if(msg.contains(bean.msgContains)){
                            return true;
                        }
                    }
                }else {
                    if(exception.getClass().getSimpleName().equals(bean.className)){
                        if(TextUtils.isEmpty(bean.msgContains)){
                            return true;
                        }else {
                            if(!TextUtils.isEmpty(msg) && msg.contains(bean.msgContains)){
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }catch (Throwable throwable){
            if(ReporterContainer.isDebug()){
                throwable.printStackTrace();
            }
            return false;
        }
    }
}
