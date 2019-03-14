package com.king.app.coolg.model.repository;

import android.database.Cursor;
import android.text.TextUtils;

import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.phone.record.scene.SceneBean;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordStarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
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

    public Observable<List<Record>> getLatestPlayableRecords(int limitStart, int limitNum) {
        return Observable.create(e -> {
            List<Record> list = getDaoSession().getRecordDao().queryBuilder()
                    .where(RecordDao.Properties.Deprecated.eq(0))
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

    public Observable<Long> getRecordCount(RecordComplexFilter filter) {
        return Observable.create(e -> {
            QueryBuilder<Record> builder = getComplexFilterBuilder(filter);
            e.onNext(builder.buildCount().count());
        });
    }

    public Observable<List<Record>> getRecords(RecordComplexFilter filter) {
        return Observable.create(e -> {
            QueryBuilder<Record> builder = getComplexFilterBuilder(filter);
            builder.offset(filter.getCursor().offset);
            builder.limit(filter.getCursor().number);
            e.onNext(builder.build().list());
        });
    }

    private QueryBuilder<Record> getComplexFilterBuilder(RecordComplexFilter filter) {
        RecordDao dao = getDaoSession().getRecordDao();
        QueryBuilder<Record> builder = dao.queryBuilder();
        if (!TextUtils.isEmpty(filter.getNameLike())) {
            builder.where(RecordDao.Properties.Name.like("%" + filter.getNameLike() + "%"));
        }
        if (!TextUtils.isEmpty(filter.getScene())) {
            builder.where(RecordDao.Properties.Scene.eq(filter.getScene()));
        }
        if (filter.getFilter() != null) {
            if (filter.getFilter().isNotDeprecated()) {
                builder.where(RecordDao.Properties.Deprecated.eq(0));
            }
            if (filter.getFilter().isBareback()) {
                builder.where(RecordDao.Properties.ScoreBareback.gt(0));
            }
            if (filter.getFilter().isInnerCum()) {
                builder.where(RecordDao.Properties.SpecialDesc.like("%inner cum%"));
            }
        }
        if (filter.getRecordType() != 0) {
            builder.where(RecordDao.Properties.Type.eq(filter.getRecordType()));
        }
        boolean hasJoin = false;
        if (filter.getStarId() != 0) {
            hasJoin = true;
            builder.join(RecordStar.class, RecordStarDao.Properties.RecordId)
                    .where(RecordStarDao.Properties.StarId.eq(filter.getStarId()));
        }
        if (filter.getStudioId() != 0) {
            hasJoin = true;
            builder.join(FavorRecord.class, FavorRecordDao.Properties.RecordId)
                    .where(FavorRecordDao.Properties.OrderId.eq(filter.getStudioId()));
        }
        sortByColumn(builder, filter.getSortType(), filter.getDesc(), hasJoin);
        return builder;
    }

    private void sortByColumn(QueryBuilder<Record> builder, int sortValue, boolean desc, boolean hasJoin) {

        String headTable = hasJoin ? "T.":"";
        if (sortValue == PreferenceValue.GDB_SR_ORDERBY_DATE) {
            builder.orderRaw(headTable + RecordDao.Properties.LastModifyTime.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCORE) {
            builder.orderRaw(headTable + RecordDao.Properties.Score.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_PASSION) {
            builder.orderRaw(headTable + RecordDao.Properties.ScorePassion.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_CUM) {
            builder.orderRaw(headTable + RecordDao.Properties.ScoreCum.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_STAR) {
            builder.orderRaw(headTable + RecordDao.Properties.ScoreStar.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL) {
            builder.orderRaw(headTable + RecordDao.Properties.ScoreFeel.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SPECIAL) {
            builder.orderRaw(headTable + RecordDao.Properties.ScoreSpecial.columnName + (desc ? " DESC":" ASC"));
        }
        else {// sort by name
            builder.orderRaw(headTable + RecordDao.Properties.Name.columnName + (desc ? " DESC":" ASC"));
        }
    }

    public long getRecordCount(int type) {
        if (type == 0) {
            return getDaoSession().getRecordDao().count();
        }
        return getDaoSession().getRecordDao().queryBuilder()
                .where(RecordDao.Properties.Type.eq(type))
                .buildCount().count();
    }

    public Observable<List<SceneBean>> getScenes(int recordType) {
        return Observable.create(e -> {
            List<SceneBean> list = new ArrayList<>();
            String sql = "SELECT scene, COUNT(scene) AS count, AVG(score) AS average, MAX(score) AS max FROM "
                    + RecordDao.TABLENAME;
            if (recordType != 0) {
                sql = sql + " WHERE type=" + recordType;
            }
            sql = sql + " GROUP BY scene ORDER BY scene";
            Cursor cursor = getDaoSession().getDatabase()
                    .rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                list.add(parseSceneBean(cursor));
            }
            e.onNext(list);
        });
    }

    private SceneBean parseSceneBean(Cursor cursor) {
        SceneBean bean = new SceneBean();
        bean.setScene(cursor.getString(0));
        bean.setNumber(cursor.getInt(1));
        bean.setAverage(cursor.getDouble(2));
        bean.setMax(cursor.getInt(3));
        return bean;
    }

    public Observable<List<Record>> getPlayableRecords(int number) {
        return Observable.create(e -> {
            List<Record> list = getDaoSession().getRecordDao().queryBuilder()
                    .where(RecordDao.Properties.Deprecated.eq(0))
                    .orderRaw("RANDOM()")
                    .limit(number)
                    .build().list();
            e.onNext(list);
        });
    }

    public Observable<List<Record>> getPlayableRecords(RecommendBean bean) {
        return Observable.create(e -> {
            RecordDao dao = getDaoSession().getRecordDao();
            StringBuffer buffer = new StringBuffer();
            if (bean.isOnlyType1v1() && !TextUtils.isEmpty(bean.getSql1v1())) {
                buildQueryFrom1v1(bean, buffer);
            }
            else if (bean.isOnlyType3w() && !TextUtils.isEmpty(bean.getSql3w())) {
                buildQueryFrom3w(bean, buffer);
            }
            buffer.append(" WHERE T.deprecated=?");
            StringBuffer whereBuffer = new StringBuffer();
            if (!TextUtils.isEmpty(bean.getSql())) {
                whereBuffer.append(" AND ").append(bean.getSql());
            }
            if (!bean.isTypeAll() && !bean.isOnlyType1v1() && !bean.isOnlyType3w()) {
                List<Integer> types = new ArrayList<>();
                if (bean.isType1v1()) {
                    types.add(DataConstants.VALUE_RECORD_TYPE_1V1);
                }
                if (bean.isType3w()) {
                    types.add(DataConstants.VALUE_RECORD_TYPE_3W);
                }
                if (bean.isTypeMulti()) {
                    types.add(DataConstants.VALUE_RECORD_TYPE_MULTI);
                }
                if (bean.isTypeTogether()) {
                    types.add(DataConstants.VALUE_RECORD_TYPE_LONG);
                }
                if (types.size() > 0) {
                    if (types.size() == 1) {
                        buffer.append(" AND T.TYPE=").append(types.get(0));
                    }
                    else {
                        buffer.append(" AND (");
                        for (int i = 0; i < types.size(); i ++) {
                            if (i > 0) {
                                buffer.append(" OR ");
                            }
                            buffer.append("T.TYPE=").append(types.get(i));
                        }
                        buffer.append(")");
                    }
                }
            }
            buffer.append(" ORDER BY RANDOM()");
            if (bean.getNumber() > 0) {
                buffer.append(" LIMIT ").append(bean.getNumber());
            }
            String sql = buffer.toString();
            DebugLog.e(sql);
            List<Record> list = dao.queryRaw(sql, new String[]{"0"});
            e.onNext(list);
        });
    }

    private void buildQueryFrom1v1(RecommendBean bean, StringBuffer buffer) {
        buffer.append(" JOIN record_type1 RT ON T.RECORD_DETAIL_ID=RT._id AND T.TYPE=").append(DataConstants.VALUE_RECORD_TYPE_1V1);
        buffer.append(" AND " + bean.getSql1v1());
    }

    private void buildQueryFrom3w(RecommendBean bean, StringBuffer buffer) {
        buffer.append(" JOIN record_type3 RT ON T.RECORD_DETAIL_ID=RT._id AND T.TYPE=").append(DataConstants.VALUE_RECORD_TYPE_3W);
        buffer.append(" AND " + bean.getSql3w());
    }
}
