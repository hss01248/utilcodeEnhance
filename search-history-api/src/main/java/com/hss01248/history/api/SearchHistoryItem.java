package com.hss01248.history.api;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/06/2022 14:49
 * @Version 1.0
 */
@Entity
public class SearchHistoryItem {

    @Id
    public String item;
    public int searchCount;
    public long updateTime;
    @Generated(hash = 1097366756)
    public SearchHistoryItem(String item, int searchCount, long updateTime) {
        this.item = item;
        this.searchCount = searchCount;
        this.updateTime = updateTime;
    }
    @Generated(hash = 1958512265)
    public SearchHistoryItem() {
    }
    public String getItem() {
        return this.item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public int getSearchCount() {
        return this.searchCount;
    }
    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }
    public long getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }


}
