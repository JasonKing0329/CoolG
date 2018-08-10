package com.king.app.coolg.model.repository;

import android.text.TextUtils;

import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

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

    public Observable<Record> getRecord(long recordId) {
        return Observable.create(e -> e.onNext(getDaoSession().getRecordDao().load(recordId)));
    }

    public Observable<List<Record>> getRecords(int sortMode, boolean desc, RecordCursor cursor, String like, String scene, RecordListFilterBean filter, int mRecordType) {
        return Observable.create(e -> {
            RecordDao dao = getDaoSession().getRecordDao();
            QueryBuilder<Record> builder = dao.queryBuilder();
            if (!TextUtils.isEmpty(like)) {
                builder.where(RecordDao.Properties.Name.like("%" + like + "%"));
            }
            if (!TextUtils.isEmpty(scene)) {
                builder.where(RecordDao.Properties.Scene.eq(scene));
            }
            if (filter != null) {
                if (filter.isNotDeprecated()) {
                    builder.where(RecordDao.Properties.Deprecated.eq(0));
                }
                if (filter.isBareback()) {
                    builder.where(RecordDao.Properties.ScoreBareback.gt(0));
                }
                if (filter.isInnerCum()) {
                    builder.where(RecordDao.Properties.SpecialDesc.like("%inner cum%"));
                }
            }
            if (mRecordType != 0) {
                builder.where(RecordDao.Properties.Type.eq(mRecordType));
            }
            sortByColumn(builder, sortMode, desc);
            builder.offset(cursor.offset);
            builder.limit(cursor.number);
            e.onNext(builder.build().list());
        });
    }

    private void sortByColumn(QueryBuilder<Record> builder, int sortValue, boolean desc) {

        if (sortValue == PreferenceValue.GDB_SR_ORDERBY_DATE) {
            builder.orderRaw(RecordDao.Properties.LastModifyTime.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCORE) {
            builder.orderRaw(RecordDao.Properties.Score.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_PASSION) {
            builder.orderRaw(RecordDao.Properties.ScorePassion.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_CUM) {
            builder.orderRaw(RecordDao.Properties.ScoreCum.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_STAR) {
            builder.orderRaw(RecordDao.Properties.ScoreStar.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL) {
            builder.orderRaw(RecordDao.Properties.ScoreFeel.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SPECIAL) {
            builder.orderRaw(RecordDao.Properties.ScoreSpecial.columnName + (desc ? " DESC":" ASC"));
        }
        else {// sort by name
            builder.orderRaw(RecordDao.Properties.Name.columnName + (desc ? " DESC":" ASC"));
        }
    }

}
