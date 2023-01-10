package com.hss01248.refresh_loadmore;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/01/2023 17:24
 * @Version 1.0
 */
public class PagerDto<T> {

   public List<T> datas;
    public boolean isLast;
    public int pageSize = 20;
    public long offset = 0;
}
