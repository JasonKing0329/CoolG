package com.king.app.coolg.model.repository;

import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayDurationDao;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayItemDao;

import org.greenrobot.greendao.DaoException;

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

    public Observable<Boolean> checkExistence(long orderId, long recordId) {
        return Observable.create(e -> {
            try {
                PlayItem item = getDaoSession().getPlayItemDao().queryBuilder()
                        .where(PlayItemDao.Properties.OrderId.eq(orderId))
                        .where(PlayItemDao.Properties.RecordId.eq(recordId))
                        .build().unique();
                if (item == null) {
                    e.onNext(true);
                }
                else {
                    e.onError(new Exception("Record is already added to target order"));
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

}
