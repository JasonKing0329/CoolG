package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 17:06
 */
@Entity(nameInDb = "favor_record")
public class FavorRecord {

    @Id(autoincrement = true)
    private Long id;

    private long orderId;

    private long recordId;

    private Date createTime;

    private Date updateTime;

    @ToOne(joinProperty = "orderId")
    private FavorRecordOrder order;

    @ToOne(joinProperty = "recordId")
    private Record record;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 135813522)
    private transient FavorRecordDao myDao;

    @Generated(hash = 1099620174)
    public FavorRecord(Long id, long orderId, long recordId, Date createTime,
            Date updateTime) {
        this.id = id;
        this.orderId = orderId;
        this.recordId = recordId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Generated(hash = 62988512)
    public FavorRecord() {
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

    public long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
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
    @Generated(hash = 1259933469)
    public FavorRecordOrder getOrder() {
        long __key = this.orderId;
        if (order__resolvedKey == null || !order__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FavorRecordOrderDao targetDao = daoSession.getFavorRecordOrderDao();
            FavorRecordOrder orderNew = targetDao.load(__key);
            synchronized (this) {
                order = orderNew;
                order__resolvedKey = __key;
            }
        }
        return order;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 858520115)
    public void setOrder(@NotNull FavorRecordOrder order) {
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

    @Generated(hash = 818274295)
    private transient Long record__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 987546320)
    public Record getRecord() {
        long __key = this.recordId;
        if (record__resolvedKey == null || !record__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordDao targetDao = daoSession.getRecordDao();
            Record recordNew = targetDao.load(__key);
            synchronized (this) {
                record = recordNew;
                record__resolvedKey = __key;
            }
        }
        return record;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1328184148)
    public void setRecord(@NotNull Record record) {
        if (record == null) {
            throw new DaoException(
                    "To-one property 'recordId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.record = record;
            recordId = record.getId();
            record__resolvedKey = recordId;
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
    @Generated(hash = 1320200546)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFavorRecordDao() : null;
    }

}
