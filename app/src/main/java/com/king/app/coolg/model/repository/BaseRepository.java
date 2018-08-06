package com.king.app.coolg.model.repository;

import com.king.app.coolg.base.CoolApplication;
import com.king.app.gdb.data.entity.DaoSession;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 17:17
 */
public abstract class BaseRepository {

    protected DaoSession getDaoSession() {
        return CoolApplication.getInstance().getDaoSession();
    }
}
