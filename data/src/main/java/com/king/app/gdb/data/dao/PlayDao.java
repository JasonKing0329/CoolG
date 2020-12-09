package com.king.app.gdb.data.dao;

import android.arch.persistence.room.Query;

import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayOrder;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:19
 */
public interface PlayDao {

    @Query("select * from play_order where id=:id")
    PlayOrder getPlayOrder(long id);

    @Query("select * from play_item where orderId=:orderId")
    List<PlayItem> getPlayItems(long orderId);

}
