package com.king.app.coolg.model.repository;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.database.Cursor;

import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.phone.star.StarStudioTag;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.FavorStarOrderDao;

import org.greenrobot.greendao.query.QueryBuilder;

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

    public Observable<List<FavorStarOrder>> getStarOrders(FavorStarOrder parent, int sortType) {
        return Observable.create(e -> {
            long parentId = parent == null ? 0:parent.getId();
            String sortFiled;
            boolean isAsc;
            switch (sortType) {
                case PreferenceValue.PHONE_ORDER_SORT_BY_ITEMS:
                    sortFiled = "number";
                    isAsc = false;
                    break;
                case PreferenceValue.PHONE_ORDER_SORT_BY_CREATE_TIME:
                    sortFiled = "create_time";
                    isAsc = false;
                    break;
                case PreferenceValue.PHONE_ORDER_SORT_BY_UPDATE_TIME:
                    sortFiled = "update_time";
                    isAsc = false;
                    break;
                default:
                    sortFiled = "name";
                    isAsc = true;
                    break;
            }
            List<FavorStarOrder> list = getDatabase().getFavDao().getStarOrdersByParent(parentId, sortFiled, isAsc);
            e.onNext(list);
        });
    }

    public Observable<List<FavorRecordOrder>> getRecordOrders(FavorRecordOrder parent, int sortType) {
        return Observable.create(e -> {
            long parentId = parent == null ? 0:parent.getId();
            String sortFiled;
            boolean isAsc;
            switch (sortType) {
                case PreferenceValue.PHONE_ORDER_SORT_BY_ITEMS:
                    sortFiled = "number";
                    isAsc = false;
                    break;
                case PreferenceValue.PHONE_ORDER_SORT_BY_CREATE_TIME:
                    sortFiled = "create_time";
                    isAsc = false;
                    break;
                case PreferenceValue.PHONE_ORDER_SORT_BY_UPDATE_TIME:
                    sortFiled = "update_time";
                    isAsc = false;
                    break;
                default:
                    sortFiled = "name";
                    isAsc = true;
                    break;
            }
            List<FavorRecordOrder> list = getDatabase().getFavDao().getRecordOrdersByParent(parentId, sortFiled, isAsc);
            e.onNext(list);
        });
    }

    public Observable<List<FavorStarOrder>> getStarOrders(long starId) {
        return Observable.create(e -> {
            List<FavorStarOrder> results = getDatabase().getFavDao().getStarOrders(starId);
            e.onNext(results);
        });
    }

    public Observable<List<FavorRecordOrder>> getRecordOrders(long recordId) {
        return Observable.create(e -> {
            List<FavorRecordOrder> results = getDatabase().getFavDao().getRecordOrders(recordId);
            e.onNext(results);
        });
    }

    public Observable<FavorStarOrder> saveStarOrderCover(long orderId, String cover) {
        return Observable.create(e -> {

            FavorStarOrder order = getDatabase().getFavDao().getFavorStarOrder(orderId);
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
            // insert to favor_star
            favorStar = new FavorStar();
            favorStar.setOrderId(orderId);
            favorStar.setStarId(starId);
            favorStar.setCreateTime(new Date());
            favorStar.setUpdateTime(new Date());
            getDaoSession().getFavorStarDao().insert(favorStar);
            getDaoSession().getFavorStarDao().detachAll();

            // update number in favor_star_order
            FavorStarOrder order = getDaoSession().getFavorStarOrderDao().load(orderId);
            order.setNumber(order.getNumber() + 1);
            getDaoSession().getFavorStarOrderDao().update(order);
            getDaoSession().getFavorStarOrderDao().detachAll();

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
            // insert to favor_record
            favorRecord = new FavorRecord();
            favorRecord.setOrderId(orderId);
            favorRecord.setRecordId(recordId);
            favorRecord.setCreateTime(new Date());
            favorRecord.setUpdateTime(new Date());
            getDaoSession().getFavorRecordDao().insert(favorRecord);
            getDaoSession().getFavorRecordDao().detachAll();

            // update number in favor_record_order
            FavorRecordOrder order = getDaoSession().getFavorRecordOrderDao().load(orderId);
            order.setNumber(order.getNumber() + 1);
            getDaoSession().getFavorRecordOrderDao().update(order);
            getDaoSession().getFavorRecordOrderDao().detachAll();

            e.onNext(favorRecord);
        });
    }

    public Observable<List<FavorStar>> getFavorStars(long orderId, int sortType) {
        return Observable.create(e -> {
            QueryBuilder<FavorStar> builder = getDaoSession().getFavorStarDao().queryBuilder();
            builder.where(FavorStarDao.Properties.OrderId.eq(orderId));
            switch (sortType) {
                case PreferenceValue.PHONE_ORDER_SORT_BY_CREATE_TIME:
                    builder.orderDesc(FavorStarDao.Properties.CreateTime);
                    break;
                case PreferenceValue.PHONE_ORDER_SORT_BY_UPDATE_TIME:
                    builder.orderDesc(FavorStarDao.Properties.UpdateTime);
                    break;
            }
            List<FavorStar> list = builder.build().list();
            e.onNext(list);
        });
    }


    public Observable<List<FavorRecord>> getFavorRecords(long orderId, int sortType) {
        return Observable.create(e -> {
            QueryBuilder<FavorRecord> builder = getDaoSession().getFavorRecordDao().queryBuilder();
            builder.where(FavorRecordDao.Properties.OrderId.eq(orderId));
            switch (sortType) {
                case PreferenceValue.PHONE_ORDER_SORT_BY_CREATE_TIME:
                    builder.orderDesc(FavorRecordDao.Properties.CreateTime);
                    break;
                case PreferenceValue.PHONE_ORDER_SORT_BY_UPDATE_TIME:
                    builder.orderDesc(FavorRecordDao.Properties.UpdateTime);
                    break;
            }
            List<FavorRecord> list = builder.build().list();
            e.onNext(list);
        });
    }

    public Observable<List<StarStudioTag>> getStudioTagByStar(long starId) {
        return Observable.create(e -> {
            FavorRecordOrder studio = getDaoSession().getFavorRecordOrderDao().queryBuilder()
                    .where(FavorRecordOrderDao.Properties.Name.eq(AppConstants.ORDER_STUDIO_NAME))
                    .build().uniqueOrThrow();

            String sql = "SELECT fodr._id, fodr.name, count(fodr._id) AS count FROM favor_order_record fodr \n" +
                    " LEFT JOIN favor_record fr ON fodr._id=fr.order_id\n" +
                    " LEFT JOIN record r ON fr.record_id=r._id\n" +
                    " LEFT JOIN record_star rs ON r._id=rs.record_id\n" +
                    " WHERE rs.star_id=? AND fodr.parent_id=?\n" +
                    " GROUP BY fodr._id\n" +
                    " ORDER BY count DESC";
            Cursor cursor = CoolApplication.getInstance().getDatabase().rawQuery(sql
                    , new String[]{String.valueOf(starId), String.valueOf(studio.getId())});
            List<StarStudioTag> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                StarStudioTag tag = new StarStudioTag();
                tag.setStudioId(cursor.getLong(0));
                tag.setName(cursor.getString(1));
                tag.setCount(cursor.getInt(2));
                list.add(tag);
            }
            e.onNext(list);
        });
    }

}
