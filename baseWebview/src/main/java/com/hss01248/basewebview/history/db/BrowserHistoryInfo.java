package com.hss01248.basewebview.history.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * @author: Administrator
 * @date: 2023/1/7
 * @desc: //todo
 */
@Entity
public class BrowserHistoryInfo {

    @Id
    public String url;

    public int isCollect;
    public String iconUrl;
    @Index
    public long updateTime;
    public long viewTimes;

    public String title;
    public String group;

    public int hasSynced;

    @Generated(hash = 1150657521)
    public BrowserHistoryInfo(String url, int isCollect, String iconUrl,
            long updateTime, long viewTimes, String title, String group,
            int hasSynced) {
        this.url = url;
        this.isCollect = isCollect;
        this.iconUrl = iconUrl;
        this.updateTime = updateTime;
        this.viewTimes = viewTimes;
        this.title = title;
        this.group = group;
        this.hasSynced = hasSynced;
    }

    @Generated(hash = 511331554)
    public BrowserHistoryInfo() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsCollect() {
        return this.isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getViewTimes() {
        return this.viewTimes;
    }

    public void setViewTimes(long viewTimes) {
        this.viewTimes = viewTimes;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getHasSynced() {
        return this.hasSynced;
    }

    public void setHasSynced(int hasSynced) {
        this.hasSynced = hasSynced;
    }

}
