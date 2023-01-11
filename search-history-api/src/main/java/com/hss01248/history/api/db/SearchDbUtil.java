package com.hss01248.history.api.db;

import android.content.Context;
import android.text.TextUtils;


import com.blankj.utilcode.util.Utils;
import com.hss01248.history.api.SearchHistoryItem;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/06/2022 14:51
 * @Version 1.0
 */
public class SearchDbUtil {

    private static DaoSession daoSession;
    public static DaoSession getDaoSession() {
        if(daoSession ==null){
            initGreenDao();
        }
        return daoSession;
    }

    private static SearchHistoryItemDao getSearchDao(){
        return getDaoSession().getSearchHistoryItemDao();
    }

    private static void initGreenDao() {
        //指定数据库存储路径
        Context context2 = Utils.getApp();
        //升级自动迁移数据的工具
        DaoMaster.OpenHelper helper = new SearchHistoryUpgradeHelper(context2, "search.db");
        Database db = helper.getWritableDb();
        //不再加密.以规避sqlitesipher在6.0以下版本的c层崩溃问题
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static List<SearchHistoryItem> loadAll(){
       return getSearchDao().queryBuilder()
               .orderDesc(SearchHistoryItemDao.Properties.UpdateTime).list();
    }

    public static void clear(){
         getSearchDao().deleteAll();
    }
    public static void delete(String key){
        getSearchDao().deleteByKey(key);
    }
    public static void addOrUpdate(String key){
        if(TextUtils.isEmpty(key)){
            return;
        }
        key = key.trim();
        if(TextUtils.isEmpty(key)){
            return;
        }
        SearchHistoryItem load = getSearchDao().load(key);
        if(load == null){
            load = new SearchHistoryItem();
            load.setItem(key);
            load.setUpdateTime(System.currentTimeMillis());
            load.searchCount++;
            getSearchDao().insert(load);
        }else {
            load.setUpdateTime(System.currentTimeMillis());
            load.searchCount++;
            getSearchDao().update(load);
        }
    }
}
