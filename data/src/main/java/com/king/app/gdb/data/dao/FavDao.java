package com.king.app.gdb.data.dao;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarOrder;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:19
 */
public interface FavDao {

    @Query("select * from favor_order_record where _id=:id")
    FavorRecordOrder getFavorRecordOrder(long id);

    @Query("select * from favor_order_record where parent_id=:orderId")
    FavorRecordOrder getFavorRecordOrderByParent(long orderId);

    @Query("select * from favor_order_record where parent_id=:orderId")
    List<FavorRecordOrder> getChildRecordOrders(long orderId);

    @Query("select * from favor_order_star where _id=:id")
    FavorStarOrder getFavorStarOrder(long id);

    @Query("select * from favor_order_star where parent_id=:orderId")
    FavorStarOrder getFavorStarOrderByParent(long orderId);

    @Query("select * from favor_order_star where parent_id=:orderId")
    List<FavorStarOrder> getChildStarOrders(long orderId);

    @Query("SELECT * FROM favor_order_star WHERE parent_id=:parentId ORDER BY CASE WHEN :isAsc = 1 THEN :orderField END ASC, CASE WHEN :isAsc = 0 THEN :orderField END DESC")
    List<FavorStarOrder> getStarOrdersByParent(long parentId, String orderField, boolean isAsc);

    @Query("SELECT * FROM favor_order_record WHERE parent_id=:parentId ORDER BY CASE WHEN :isAsc = 1 THEN :orderField END ASC, CASE WHEN :isAsc = 0 THEN :orderField END DESC")
    List<FavorRecordOrder> getRecordOrdersByParent(long parentId, String orderField, boolean isAsc);

    @Query("select fos.* from favor_order_star fos join favor_star fs on fos._id = fs.order_id where fs.star_id =:starId")
    List<FavorStarOrder> getStarOrders(long starId);

    @Query("select fos.* from favor_order_record fos join favor_record fs on fos._id = fs.order_id where fs.record_id =:recordId")
    List<FavorRecordOrder> getRecordOrders(long recordId);

    @RawQuery
    List<FavorStarOrder> getBySql(SupportSQLiteQuery sql);
}
