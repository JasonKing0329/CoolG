package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

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

    @ToOne(joinProperty = "orderId")
    private FavorStarOrder order;

    @ToOne(joinProperty = "starId")
    private Star star;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 737124733)
    private transient FavorStarDao myDao;

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

    @Generated(hash = 219913283)
    private transient Long order__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 549443455)
    public FavorStarOrder getOrder() {
        long __key = this.orderId;
        if (order__resolvedKey == null || !order__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FavorStarOrderDao targetDao = daoSession.getFavorStarOrderDao();
            FavorStarOrder orderNew = targetDao.load(__key);
            synchronized (this) {
                order = orderNew;
                order__resolvedKey = __key;
            }
        }
        return order;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1852166757)
    public void setOrder(@NotNull FavorStarOrder order) {
        if (order == null) {
            throw new DaoException(
                    "To-one property 'orderId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.order = order;
            orderId = order.getId();
            order__resolvedKey = orderId;
        }
    }

    @Generated(hash = 758316439)
    private transient Long star__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1453149840)
    public Star getStar() {
        long __key = this.starId;
        if (star__resolvedKey == null || !star__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StarDao targetDao = daoSession.getStarDao();
            Star starNew = targetDao.load(__key);
            synchronized (this) {
                star = starNew;
                star__resolvedKey = __key;
            }
        }
        return star;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 133780265)
    public void setStar(@NotNull Star star) {
        if (star == null) {
            throw new DaoException(
                    "To-one property 'starId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.star = star;
            starId = star.getId();
            star__resolvedKey = starId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1946651135)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFavorStarDao() : null;
    }

}
