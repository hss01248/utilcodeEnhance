package com.hss01248.basewebview.history.db;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;


import com.blankj.utilcode.util.Utils;
import com.hss01248.refresh_loadmore.PagerDto;

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


    public static void addHistory(String title,String url,String iconUrl){
        BrowserHistoryInfo load = getDaoSession().getBrowserHistoryInfoDao().queryBuilder()
                .where(BrowserHistoryInfoDao.Properties.Url.eq(url),
                        BrowserHistoryInfoDao.Properties.IsCollect.eq(0))
                .unique();
        if(load != null){
            load.viewTimes++;
            load.updateTime = System.currentTimeMillis();
            if(!TextUtils.isEmpty(iconUrl)){
                load.iconUrl = iconUrl;
            }
            load.title = title;
            getDaoSession().getBrowserHistoryInfoDao().update(load);
        }else {
            load = new BrowserHistoryInfo();
            load.isCollect = 0;
            load.iconUrl = iconUrl;
            load.title = title;
            load.url = url;
            load.viewTimes = 1;
            load.updateTime = System.currentTimeMillis();
            getDaoSession().getBrowserHistoryInfoDao().insert(load);
        }
    }

    public static void addCollect(String title,String url,String iconUrl){
        BrowserHistoryInfo load = getDaoSession().getBrowserHistoryInfoDao().queryBuilder()
                .where(BrowserHistoryInfoDao.Properties.Url.eq(url),
                        BrowserHistoryInfoDao.Properties.IsCollect.eq(1))
                .unique();
        if(load != null){
            load.viewTimes++;
            load.updateTime = System.currentTimeMillis();
            if(!TextUtils.isEmpty(iconUrl)){
                load.iconUrl = iconUrl;
            }
            load.title = title;
            getDaoSession().getBrowserHistoryInfoDao().update(load);
        }else {
            load = new BrowserHistoryInfo();
            load.isCollect = 1;
            load.iconUrl = iconUrl;
            load.title = title;
            load.url = url;
            load.viewTimes = 1;
            load.updateTime = System.currentTimeMillis();
            getDaoSession().getBrowserHistoryInfoDao().insert(load);
        }
    }

    public static PagerDto<BrowserHistoryInfo> loadHistory(PagerDto pagerDto){
        return loadByPager(pagerDto,false);
    }

    public  static PagerDto<BrowserHistoryInfo> loadCollect(PagerDto pagerDto){

        return loadByPager(pagerDto,true);
    }
   public static PagerDto<BrowserHistoryInfo> loadByPager(PagerDto pagerDto,boolean isCollect){
        List<BrowserHistoryInfo> list = getDaoSession().getBrowserHistoryInfoDao().queryBuilder()
                .where(BrowserHistoryInfoDao.Properties.IsCollect.eq(isCollect? 1: 0))
                .orderDesc(BrowserHistoryInfoDao.Properties.UpdateTime)
                .limit(pagerDto.pageSize)
                .offset((int) pagerDto.offset)
                .list();
        PagerDto<BrowserHistoryInfo> pagerDto1 = new PagerDto<BrowserHistoryInfo>();
        pagerDto1.isLast = list.size() < pagerDto.pageSize;
        pagerDto1.datas = list;
        //在这里自动计算偏移,在界面里直接透传即可
        pagerDto1.offset = pagerDto.offset+ list.size();
        pagerDto1.pageSize = pagerDto.pageSize;
        return pagerDto1;
    }





}
