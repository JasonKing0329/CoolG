package com.king.app.coolg.model.repository;

import android.text.TextUtils;

import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayDurationDao;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayItemDao;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordStarDao;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType1v1Dao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 10:35
 */
public class PlayRepository extends BaseRepository {

    public boolean isItemExist(long orderId, long recordId) {
        long count = getDaoSession().getPlayItemDao().queryBuilder()
                .where(PlayItemDao.Properties.OrderId.eq(orderId))
                .where(PlayItemDao.Properties.RecordId.eq(recordId))
                .buildCount().count();
        return count > 0;
    }

    public boolean isExist(long orderId, long recordId) {
        PlayItem item = getDaoSession().getPlayItemDao().queryBuilder()
                .where(PlayItemDao.Properties.OrderId.eq(orderId))
                .where(PlayItemDao.Properties.RecordId.eq(recordId))
                .build().unique();
        return item != null;
    }

    public Observable<Boolean> checkExistence(long orderId, long recordId) {
        return Observable.create(e -> {
            try {
                if (isExist(orderId, recordId)) {
                    e.onError(new Exception("Record is already added to target order"));
                }
                else {
                    e.onNext(true);
                }
            } catch (DaoException ex) {
                ex.printStackTrace();
                e.onError(new Exception("Find more than 1 items existed in database"));
            }
        });
    }

    public Observable<PlayItem> insertOrReplacePlayItem(PlayItem item) {
        return Observable.create(e -> {
            getDaoSession().getPlayItemDao().insertOrReplace(item);
            getDaoSession().getPlayItemDao().detachAll();
            e.onNext(item);
        });
    }

    public Observable<PlayDuration> getDuration(long recordId) {
        return Observable.create(e -> e.onNext(getDurationInstance(recordId)));
    }

    public PlayDuration getDurationInstance(long recordId) {

        PlayDuration duration = null;
        try {
            duration = getDaoSession().getPlayDurationDao().queryBuilder()
                    .where(PlayDurationDao.Properties.RecordId.eq(recordId))
                    .build().unique();
        } catch (DaoException e) {
            e.printStackTrace();
            getDaoSession().getPlayDurationDao().queryBuilder()
                    .where(PlayDurationDao.Properties.RecordId.eq(recordId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getPlayDurationDao().detachAll();
        }
        if (duration == null) {
            duration = new PlayDuration();
            duration.setRecordId(recordId);
        }
        return duration;
    }

    public Observable<Boolean> deleteDuration(long recordId) {
        return Observable.create(e -> {
            getDaoSession().getPlayDurationDao().queryBuilder()
                    .where(PlayDurationDao.Properties.RecordId.eq(recordId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getPlayDurationDao().detachAll();
            e.onNext(true);
        });
    }

    public Observable<Boolean> hasPlayList(long orderId) {
        return Observable.create(e -> {
            long count = getDaoSession().getPlayItemDao().queryBuilder()
                    .where(PlayItemDao.Properties.OrderId.eq(orderId))
                    .buildCount().count();
            e.onNext(count > 0);
        });
    }

    public Observable<List<PlayItem>> getPlayItems(long orderId) {
        return Observable.create(e -> e.onNext(getDaoSession().getPlayItemDao().queryBuilder().where(PlayItemDao.Properties.OrderId.eq(orderId)).list()));
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

    public Observable<List<Record>> getPlayableRecords(int number, String where) {
        return Observable.create(e -> {
            RecordDao dao = getDaoSession().getRecordDao();
            String sql = " WHERE " + where + " AND deprecated=? ORDER BY RANDOM() LIMIT " + number;
            List<Record> list = dao.queryRaw(sql, new String[]{"0"});
            e.onNext(list);
        });
    }

    public Observable<List<Record>> getPlayableRecords(int number, RecommendBean bean) {
        return Observable.create(e -> {
            RecordDao dao = getDaoSession().getRecordDao();
            StringBuffer buffer = new StringBuffer();
            if (bean.isWithFkType()) {
                buffer.append(" JOIN record_type1 RT ON T.RECORD_DETAIL_ID=RT._id AND T.TYPE=").append(DataConstants.VALUE_RECORD_TYPE_1V1);
                if (bean.isFkType1()) {
                    buffer.append(" AND RT.SCORE_FK_TYPE1>0");
                }
                if (bean.isFkType2()) {
                    buffer.append(" AND RT.SCORE_FK_TYPE2>0");
                }
                if (bean.isFkType3()) {
                    buffer.append(" AND RT.SCORE_FK_TYPE3>0");
                }
                if (bean.isFkType4()) {
                    buffer.append(" AND RT.SCORE_FK_TYPE4>0");
                }
                if (bean.isFkType5()) {
                    buffer.append(" AND RT.SCORE_FK_TYPE5>0");
                }
                if (bean.isFkType6()) {
                    buffer.append(" AND RT.SCORE_FK_TYPE6>0");
                }
            }
            buffer.append(" WHERE T.deprecated=?");
            StringBuffer whereBuffer = new StringBuffer();
            if (!TextUtils.isEmpty(bean.getSql())) {
                whereBuffer.append(" AND ").append(bean.getSql());
            }
            if (!bean.isWithFkType() && !bean.isTypeAll()) {
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
            buffer.append(" ORDER BY RANDOM() LIMIT ").append(number);
            String sql = buffer.toString();
            DebugLog.e(sql);
            List<Record> list = dao.queryRaw(sql, new String[]{"0"});
            e.onNext(list);
        });
    }

    public int getNotDeprecatedCount(long starId) {
        return (int) getNotDeprecatedBuilder(starId).buildCount().count();
    }

    private QueryBuilder<Record> getNotDeprecatedBuilder(long starId) {
        QueryBuilder<Record> builder = getDaoSession().getRecordDao().queryBuilder();
        builder.join(RecordStar.class, RecordStarDao.Properties.RecordId)
                .where(RecordStarDao.Properties.StarId.eq(starId));
        builder.where(RecordDao.Properties.Deprecated.eq(0));
        return builder;
    }

    public Observable<List<PlayItemViewBean>> getStarPlayItems(long starId) {
        return Observable.create(e -> {
            QueryBuilder<Record> builder = getNotDeprecatedBuilder(starId);
            List<Record> records = builder.build().list();
            List<PlayItemViewBean> list = new ArrayList<>();
            for (Record record:records) {
                PlayItemViewBean item = new PlayItemViewBean();
                item.setRecord(record);
                item.setCover(ImageProvider.getRecordRandomPath(record.getName(), null));
                list.add(item);
            }
            e.onNext(list);
        });
    }

    public Observable<Boolean> deletePlayItem(long orderId, long recordId) {
        return Observable.create(e -> {
            getDaoSession().getPlayItemDao().queryBuilder()
                    .where(PlayItemDao.Properties.OrderId.eq(orderId))
                    .where(PlayItemDao.Properties.RecordId.eq(recordId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getPlayItemDao().detachAll();
            e.onNext(true);
        });
    }

    public Observable<Boolean> deleteAllPlayItems(long orderId) {
        return Observable.create(e -> {
            getDaoSession().getPlayItemDao().queryBuilder()
                    .where(PlayItemDao.Properties.OrderId.eq(orderId))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            getDaoSession().getPlayItemDao().detachAll();
            e.onNext(true);
        });
    }

}
