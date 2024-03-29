package com.hss01248.history.api.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hss01248.history.api.SearchHistoryItem;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEARCH_HISTORY_ITEM".
*/
public class SearchHistoryItemDao extends AbstractDao<SearchHistoryItem, String> {

    public static final String TABLENAME = "SEARCH_HISTORY_ITEM";

    /**
     * Properties of entity SearchHistoryItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Item = new Property(0, String.class, "item", true, "ITEM");
        public final static Property SearchCount = new Property(1, int.class, "searchCount", false, "SEARCH_COUNT");
        public final static Property UpdateTime = new Property(2, long.class, "updateTime", false, "UPDATE_TIME");
    }


    public SearchHistoryItemDao(DaoConfig config) {
        super(config);
    }
    
    public SearchHistoryItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEARCH_HISTORY_ITEM\" (" + //
                "\"ITEM\" TEXT PRIMARY KEY NOT NULL ," + // 0: item
                "\"SEARCH_COUNT\" INTEGER NOT NULL ," + // 1: searchCount
                "\"UPDATE_TIME\" INTEGER NOT NULL );"); // 2: updateTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEARCH_HISTORY_ITEM\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SearchHistoryItem entity) {
        stmt.clearBindings();
 
        String item = entity.getItem();
        if (item != null) {
            stmt.bindString(1, item);
        }
        stmt.bindLong(2, entity.getSearchCount());
        stmt.bindLong(3, entity.getUpdateTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SearchHistoryItem entity) {
        stmt.clearBindings();
 
        String item = entity.getItem();
        if (item != null) {
            stmt.bindString(1, item);
        }
        stmt.bindLong(2, entity.getSearchCount());
        stmt.bindLong(3, entity.getUpdateTime());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public SearchHistoryItem readEntity(Cursor cursor, int offset) {
        SearchHistoryItem entity = new SearchHistoryItem( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // item
            cursor.getInt(offset + 1), // searchCount
            cursor.getLong(offset + 2) // updateTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SearchHistoryItem entity, int offset) {
        entity.setItem(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setSearchCount(cursor.getInt(offset + 1));
        entity.setUpdateTime(cursor.getLong(offset + 2));
     }
    
    @Override
    protected final String updateKeyAfterInsert(SearchHistoryItem entity, long rowId) {
        return entity.getItem();
    }
    
    @Override
    public String getKey(SearchHistoryItem entity) {
        if(entity != null) {
            return entity.getItem();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SearchHistoryItem entity) {
        return entity.getItem() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
