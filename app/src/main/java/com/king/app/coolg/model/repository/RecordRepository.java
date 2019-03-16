package com.king.app.coolg.model.repository;

import android.database.Cursor;
import android.text.TextUtils;

import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.phone.record.scene.SceneBean;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
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

    public Observable<Integer> getRecordCount(RecordComplexFilter filter) {
        return Observable.create(e -> {
            StringBuffer buffer = getComplexFilterBuilder(filter);
            String sql = buffer.toString();
            DebugLog.e(sql);
            List<Record> list = getDaoSession().getRecordDao().queryRaw(sql, new String[]{});
            e.onNext(list.size());
        });
    }

    public Observable<List<Record>> getRecords(RecordComplexFilter filter) {
        return Observable.create(e -> {
            StringBuffer buffer = getComplexFilterBuilder(filter);
            if (filter.getCursor() != null) {
                buffer.append(" LIMIT ").append(filter.getCursor().offset).append(",").append(filter.getCursor().number);
            }
            String sql = buffer.toString();
            DebugLog.e(sql);
            List<Record> list = getDaoSession().getRecordDao().queryRaw(sql, new String[]{});
            e.onNext(list);
            e.onComplete();
        });
    }

    private StringBuffer getComplexFilterBuilder(RecordComplexFilter filter) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer whereBuffer = new StringBuffer();
        RecommendBean recommendBean = filter.getFilter();
        // 先处理JOIN
        if (recommendBean != null) {
            if (recommendBean.isOnlyType1v1()
                    && !TextUtils.isEmpty(recommendBean.getSql1v1())) {
                buildQueryFrom1v1(recommendBean, buffer);
            }
            else if (recommendBean.isOnlyType3w()
                    && !TextUtils.isEmpty(recommendBean.getSql3w())) {
                buildQueryFrom3w(recommendBean, buffer);
            }
        }
        if (filter.getStarId() != 0) {
            buildQueryJoinStar(filter.getStarId(), buffer);
        }
        if (filter.getStudioId() != 0) {
            buildQueryJoinStudio(filter.getStudioId(), buffer);
        }
        // 再处理WHERE
        if (recommendBean != null) {
            if (recommendBean.isOnline()) {
                appendWhere(whereBuffer, "T.deprecated=0");
            }
            if (!TextUtils.isEmpty(filter.getFilter().getSql())) {
                appendWhere(whereBuffer, recommendBean.getSql());
            }
        }
        if (!TextUtils.isEmpty(filter.getNameLike())) {
            appendWhere(whereBuffer, "NAME LIKE '%").append(filter.getNameLike()).append("%'");
        }
        if (!TextUtils.isEmpty(filter.getScene())) {
            appendWhere(whereBuffer, "SCENE='").append(filter.getScene()).append("'");
        }
        // 以RecommendBean里的type为准
        if (filter.getRecordType() == null) {
            if (filter.getFilter() != null) {
                appendType(whereBuffer, filter.getFilter());
            }
        }
        else {
            // 0代表全部
            if (filter.getRecordType() != 0) {
                appendWhere(whereBuffer, "TYPE=").append(filter.getRecordType());
            }
        }
        buffer.append(whereBuffer.toString());

        sortByColumn(buffer, filter.getSortType(), filter.getDesc());

        return buffer;
    }

    private void sortByColumn(StringBuffer buffer, int sortValue, boolean desc) {
        if (sortValue == PreferenceValue.GDB_SR_ORDERBY_DATE) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.LastModifyTime.columnName).append(desc ? " DESC":" ASC");
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCORE) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.Score.columnName).append(desc ? " DESC":" ASC");
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_PASSION) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.ScorePassion.columnName).append(desc ? " DESC":" ASC");
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_CUM) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.ScoreCum.columnName).append(desc ? " DESC":" ASC");
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_STAR) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.ScoreStar.columnName).append(desc ? " DESC":" ASC");
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.ScoreFeel.columnName).append(desc ? " DESC":" ASC");
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SPECIAL) {
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.ScoreSpecial.columnName).append(desc ? " DESC":" ASC");
        }
        else {// sort by name
            buffer.append(" ORDER BY T.").append(RecordDao.Properties.Name.columnName).append(desc ? " DESC":" ASC");
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

    public Observable<List<Record>> getRecordsBy(int number, boolean isOnline) {
        return Observable.create(e -> {
            QueryBuilder<Record> builder = getDaoSession().getRecordDao().queryBuilder();
            if (isOnline) {
                builder.where(RecordDao.Properties.Deprecated.eq(0));
            }
            builder.orderRaw("RANDOM()");
            if (number > 0) {
                builder.limit(number);
            }
            List<Record> list = builder.build().list();
            e.onNext(list);
        });
    }

    private boolean isBuildType1v1(RecommendBean bean) {
        if (bean == null) {
            return false;
        }
        return bean.isOnlyType1v1() && !TextUtils.isEmpty(bean.getSql1v1());
    }

    private boolean isBuildType3w(RecommendBean bean) {
        if (bean == null) {
            return false;
        }
        return bean.isOnlyType3w() && !TextUtils.isEmpty(bean.getSql3w());
    }

    private void appendType(StringBuffer where, RecommendBean bean) {
        if (!bean.isTypeAll() && !isBuildType1v1(bean) && !isBuildType3w(bean)) {
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
                    appendWhere(where, "T.TYPE=").append(types.get(0));
                }
                else {
                    appendWhere(where, "(");
                    for (int i = 0; i < types.size(); i ++) {
                        if (i > 0) {
                            where.append(" OR ");
                        }
                        where.append("T.TYPE=").append(types.get(i));
                    }
                    where.append(")");
                }
            }
        }
    }

    public Observable<List<Record>> getRecordsBy(RecommendBean bean) {
        return Observable.create(e -> {
            RecordDao dao = getDaoSession().getRecordDao();
            StringBuffer buffer = new StringBuffer();
            if (isBuildType1v1(bean)) {
                buildQueryFrom1v1(bean, buffer);
            }
            else if (isBuildType3w(bean)) {
                buildQueryFrom3w(bean, buffer);
            }
            StringBuffer where = new StringBuffer();
            if (bean.isOnline()) {
                appendWhere(where, "T.deprecated=0");
            }
            if (!TextUtils.isEmpty(bean.getSql())) {
                appendWhere(where, bean.getSql());
            }
            // record type
            appendType(where, bean);

            buffer.append(where.toString());

            buffer.append(" ORDER BY RANDOM()");
            if (bean.getNumber() > 0) {
                buffer.append(" LIMIT ").append(bean.getNumber());
            }
            String sql = buffer.toString();
            DebugLog.e(sql);
            List<Record> list = dao.queryRaw(sql, new String[]{});
            e.onNext(list);
            e.onComplete();
        });
    }

    private StringBuffer appendWhere(StringBuffer where, String condition) {
        if (where.length() == 0) {
            where.append(" WHERE ").append(condition);
        }
        else {
            where.append(" AND ").append(condition);
        }
        return where;
    }

    private void buildQueryFrom1v1(RecommendBean bean, StringBuffer buffer) {
        buffer.append(" JOIN record_type1 RT ON T.RECORD_DETAIL_ID=RT._id AND T.TYPE=").append(DataConstants.VALUE_RECORD_TYPE_1V1);
        buffer.append(" AND " + bean.getSql1v1());
    }

    private void buildQueryFrom3w(RecommendBean bean, StringBuffer buffer) {
        buffer.append(" JOIN record_type3 RT ON T.RECORD_DETAIL_ID=RT._id AND T.TYPE=").append(DataConstants.VALUE_RECORD_TYPE_3W);
        buffer.append(" AND " + bean.getSql3w());
    }

    private void buildQueryJoinStar(long starId, StringBuffer buffer) {
        buffer.append(" JOIN record_star RS ON RS.RECORD_ID=T._id AND RS.STAR_ID=").append(starId);
    }

    private void buildQueryJoinStudio(long studioId, StringBuffer buffer) {
        buffer.append(" JOIN favor_record FR ON FR.RECORD_ID=T._id AND FR.ORDER_ID=").append(studioId);
    }
}
