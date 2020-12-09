package com.king.app.gdb.data.dao;

import android.arch.persistence.room.Query;

import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorStarOrder;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:19
 */
public interface FavDao {

    @Query("select * from favor_order_record where id=:id")
    FavorRecordOrder getFavorRecordOrder(long id);

    @Query("select * from favor_order_record where parentId=:orderId")
    FavorRecordOrder getFavorRecordOrderByParent(long orderId);

    @Query("select * from favor_order_record where parentId=:orderId")
    List<FavorRecordOrder> getChildRecordOrders(long orderId);

    @Query("select * from favor_order_star where id=:id")
    FavorStarOrder getFavorStarOrder(long id);

    @Query("select * from favor_order_star where parentId=:orderId")
    FavorStarOrder getFavorStarOrderByParent(long orderId);

    @Query("select * from favor_order_star where parentId=:orderId")
    List<FavorStarOrder> getChildStarOrders(long orderId);
}
