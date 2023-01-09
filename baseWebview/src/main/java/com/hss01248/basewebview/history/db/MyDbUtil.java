package com.hss01248.basewebview.history.db;

import android.app.Application;
import android.content.Context;


import com.blankj.utilcode.util.Utils;

import org.greenrobot.greendao.database.Database;

import java.util.List;


public class MyDbUtil {

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private static void initGreenDao(Application context) {
        //指定数据库存储路径
       Context context2 = context;
       //升级自动迁移数据的工具
        DaoMaster.OpenHelper helper = new MySQLiteUpgradeOpenHelper(context2, "browser_history.db");
        Database db = helper.getWritableDb();
        //不再加密.以规避sqlitesipher在6.0以下版本的c层崩溃问题
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        //兼容旧数据迁移情况
        //moveOldData(context);
    }


    private static DaoSession  daoSession;
    public static DaoSession getDaoSession() {
        if(daoSession ==null){
            initGreenDao(Utils.getApp());
        }
        return daoSession;
    }

    /* static List<BrowserHistoryInfo> getAll(int hostType, String countCode){
       return getDaoSession().getBrowserHistoryInfoDao().queryBuilder().where(BrowserHistoryInfoDao.Properties.HostType.eq(hostType)
                ,DebugAccountDao.Properties.CountryCode.eq(countCode))
                .orderDesc(DebugAccountDao.Properties.UsedNum).list();
    }*/


}
