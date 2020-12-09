package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 17:31
 */
@Entity(tableName = "video_cover_order")
public class VideoCoverPlayOrder {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private long orderId;

    @Ignore
    private PlayOrder order;// PlayDao.getPlayOrder(orderId)

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public PlayOrder getOrder() {
        return order;
    }

    public void setOrder(PlayOrder order) {
        this.order = order;
    }
}
