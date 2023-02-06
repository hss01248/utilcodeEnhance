package com.hss01248.basewebview.history.ui;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.basewebview.history.db.BrowserHistoryInfo;
import com.hss01248.basewebview.history.db.MyDbUtil;
import com.hss01248.refresh_loadmore.ILoadData;
import com.hss01248.refresh_loadmore.PagerDto;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 19:54
 * @Version 1.0
 */
public class LoadDataByHistoryDb implements ILoadData<BrowserHistoryInfo> {

    public LoadDataByHistoryDb(boolean isCollect) {
        this.isCollect = isCollect;
    }

    boolean isCollect;
    @Override
    public void queryData(PagerDto<BrowserHistoryInfo> pager, MyCommonCallback<PagerDto<BrowserHistoryInfo>> callback) {
        try {
            PagerDto<BrowserHistoryInfo> browserHistoryInfoPagerDto = MyDbUtil.loadByPager(pager,isCollect);
            callback.onSuccess(browserHistoryInfoPagerDto);
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            callback.onError(throwable.getClass().getSimpleName(),throwable.getMessage(),throwable);
        }
    }

    @Override
    public boolean deleteData(BrowserHistoryInfo data, int position) {
        MyDbUtil.getDaoSession().getBrowserHistoryInfoDao().delete(data);
        return true;
    }


}
