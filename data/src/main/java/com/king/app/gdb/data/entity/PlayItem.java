package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 9:51
 */
@Entity(nameInDb = "play_item")
public class PlayItem {

    @Id(autoincrement = true)
    private Long id;

    private long orderId;

    private long recordId;

    private String url;

    private int index;

    @ToOne(joinProperty = "orderId")
    private PlayOrder order;

    @ToOne(joinProperty = "recordId")
    private Record record;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 444421198)
    private transient PlayItemDao myDao;

    @Generated(hash = 1962473373)
    public PlayItem(Long id, long orderId, long recordId, String url, int index) {
        this.id = id;
        this.orderId = orderId;
        this.recordId = recordId;
        this.url = url;
        this.index = index;
    }

    @Generated(hash = 1251437942)
    public PlayItem() {
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Generated(hash = 219913283)
    private transient Long order__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 501133899)
    public PlayOrder getOrder() {
        long __key = this.orderId;
        if (order__resolvedKey == null || !order__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlayOrderDao targetDao = daoSession.getPlayOrderDao();
            PlayOrder orderNew = targetDao.load(__key);
            synchronized (this) {
                order = orderNew;
                order__resolvedKey = __key;
            }
        }
        return order;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2063693434)
    public void setOrder(@NotNull PlayOrder order) {
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
    @Generated(hash = 342404159)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlayItemDao() : null;
    }

}
