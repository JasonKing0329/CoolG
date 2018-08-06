package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 17:06
 */
@Entity(nameInDb = "favor_star")
public class FavorStar {

    @Id(autoincrement = true)
    private Long id;

    private long orderId;

    private long starId;

    private Date createTime;

    private Date updateTime;

    @Generated(hash = 1804404017)
    public FavorStar(Long id, long orderId, long starId, Date createTime,
            Date updateTime) {
        this.id = id;
        this.orderId = orderId;
        this.starId = starId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 792802382)
    public FavorStar() {
    }

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
}
