package com.king.app.coolg.model.repository;

import com.king.app.gdb.data.entity.GPropertiesDao;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 10:09
 */
public class PropertyRepository extends BaseRepository {

    public String getVersion() {
        return getDaoSession().getGPropertiesDao().queryBuilder()
                .where(GPropertiesDao.Properties.Key.eq("version"))
                .build().unique().getValue();
    }
}
