package com.king.app.coolg.model.repository;

import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayDurationDao;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayItemDao;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordStarDao;

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
