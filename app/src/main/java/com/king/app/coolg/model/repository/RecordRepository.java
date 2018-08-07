package com.king.app.coolg.model.repository;

import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.param.DataConstants;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 17:14
 */
public class RecordRepository extends BaseRepository {

    public Observable<List<Record>> getLatestRecords(int limitStart, int limitNum) {
        return Observable.create(e -> {
            List<Record> list = getDaoSession().getRecordDao().queryBuilder()
                    .orderDesc(RecordDao.Properties.LastModifyTime)
                    .offset(limitStart)
                    .limit(limitNum)
                    .build().list();
            e.onNext(list);
        });
    }

    public Observable<List<Record>> getAll() {
        return Observable.create(e -> e.onNext(getDaoSession().getRecordDao().loadAll()));
    }

}
