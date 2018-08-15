package com.king.app.coolg.model.repository;

import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/15 9:50
 */
public class OrderRepository extends BaseRepository {

    public Observable<List<FavorStarOrder>> getStarOrders(long starId) {
        return Observable.create(e -> {
            List<FavorStar> list = getDaoSession().getFavorStarDao().queryBuilder()
                    .where(FavorStarDao.Properties.StarId.eq(starId))
                    .build().list();
            List<FavorStarOrder> results = new ArrayList<>();
            for (FavorStar fs:list) {
                results.add(fs.getOrder());
            }
            e.onNext(results);
        });
    }

    public Observable<List<FavorRecordOrder>> getRecordOrders(long recordId) {
        return Observable.create(e -> {
            List<FavorRecord> list = getDaoSession().getFavorRecordDao().queryBuilder()
                    .where(FavorRecordDao.Properties.RecordId.eq(recordId))
                    .build().list();
            List<FavorRecordOrder> results = new ArrayList<>();
            for (FavorRecord fs:list) {
                results.add(fs.getOrder());
            }
            e.onNext(results);
        });
    }

    public Observable<FavorStarOrder> saveStarOrderCover(long orderId, String cover) {
        return Observable.create(e -> {
            FavorStarOrder order = getDaoSession().getFavorStarOrderDao().load(orderId);
            order.setCoverUrl(cover);
            getDaoSession().getFavorStarOrderDao().save(order);
            e.onNext(order);
        });
    }

    public Observable<FavorRecordOrder> saveRecordOrderCover(long orderId, String cover) {
        return Observable.create(e -> {
            FavorRecordOrder order = getDaoSession().getFavorRecordOrderDao().load(orderId);
            order.setCoverUrl(cover);
            getDaoSession().getFavorRecordOrderDao().save(order);
            e.onNext(order);
        });
    }

    public Observable<FavorStar> addFavorStar(long orderId, long starId) {
        return Observable.create(e -> {
            FavorStar favorStar = getDaoSession().getFavorStarDao().queryBuilder()
                    .where(FavorStarDao.Properties.StarId.eq(starId))
                    .where(FavorStarDao.Properties.OrderId.eq(orderId))
                    .build().unique();
            if (favorStar != null) {
                e.onError(new Exception("Target is already in order"));
                return;
            }
            favorStar = new FavorStar();
            favorStar.setOrderId(orderId);
            favorStar.setStarId(starId);
            favorStar.setCreateTime(new Date());
            favorStar.setUpdateTime(new Date());
            getDaoSession().getFavorStarDao().insert(favorStar);
            getDaoSession().getFavorStarDao().detachAll();
            e.onNext(favorStar);
        });
    }

    public Observable<FavorRecord> addFavorRecord(long orderId, long recordId) {
        return Observable.create(e -> {
            FavorRecord favorRecord = getDaoSession().getFavorRecordDao().queryBuilder()
                    .where(FavorRecordDao.Properties.RecordId.eq(recordId))
                    .where(FavorRecordDao.Properties.OrderId.eq(orderId))
                    .build().unique();
            if (favorRecord != null) {
                e.onError(new Exception("Target is already in order"));
                return;
            }
            favorRecord = new FavorRecord();
            favorRecord.setOrderId(orderId);
            favorRecord.setRecordId(recordId);
            favorRecord.setCreateTime(new Date());
            favorRecord.setUpdateTime(new Date());
            getDaoSession().getFavorRecordDao().insert(favorRecord);
            getDaoSession().getFavorRecordDao().detachAll();
            e.onNext(favorRecord);
        });
    }
}
