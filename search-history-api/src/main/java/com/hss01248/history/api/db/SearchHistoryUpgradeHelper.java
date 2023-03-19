package com.hss01248.history.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

/**
 * @Despciption todo
 * @Author hss
 * @Date 20/06/2022 14:55
 * @Version 1.0
 */
public class SearchHistoryUpgradeHelper extends DaoMaster.OpenHelper {
    public SearchHistoryUpgradeHelper(Context context, String name) {
        super(context, name);
    }

    public SearchHistoryUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, SearchHistoryItemDao.class);
    }
}