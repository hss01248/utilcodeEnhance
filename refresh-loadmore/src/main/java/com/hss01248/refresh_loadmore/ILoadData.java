package com.hss01248.refresh_loadmore;

import com.hss.utils.enhance.api.MyCommonCallback;

public interface ILoadData<T> {

    void loadData(PagerDto<T> pager, MyCommonCallback<PagerDto<T>> callback);

}
