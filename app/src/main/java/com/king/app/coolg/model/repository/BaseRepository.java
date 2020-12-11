package com.king.app.coolg.model.repository;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;

import com.king.app.coolg.base.CoolApplication;
import com.king.app.gdb.data.AppDatabase;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 17:17
 */
public abstract class BaseRepository {

    protected AppDatabase getDatabase() {
        return AppDatabase.getInstance(CoolApplication.getInstance());
    }

    protected SupportSQLiteQuery formatQuery(String sql, String[] args) {
        return new SimpleSQLiteQuery(sql, args);
    }

}
