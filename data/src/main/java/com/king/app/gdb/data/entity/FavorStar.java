package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 17:06
 */
@Entity(tableName = "favor_star")
public class FavorStar {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private long orderId;

    private long starId;

    private Date createTime;

    private Date updateTime;

    @Ignore
    private FavorStarOrder order;// FavDao.getFavorStarOrder(orderId)

    @Ignore
    private Star star;// StarDao.getStar(starId)

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

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public FavorStarOrder getOrder() {
        return order;
    }

    public void setOrder(FavorStarOrder order) {
        this.order = order;
    }

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }
}
