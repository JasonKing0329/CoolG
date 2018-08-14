package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 16:59
 */
@Entity(nameInDb = "favor_order_record")
public class FavorRecordOrder {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private String coverUrl;

    private int number;

    private int sortSeq;

    private Date createTime;

    private Date updateTime;

    private long parentId;

    @ToMany(referencedJoinProperty = "parentId")
    private List<FavorRecordOrder> childList;

    @ToOne(joinProperty = "parentId")
    private FavorRecordOrder parent;

    @ToMany
    @JoinEntity(
            entity = FavorRecord.class,
            sourceProperty = "orderId",
            targetProperty = "recordId"
    )
    private List<Record> recordList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 702593782)
    private transient FavorRecordOrderDao myDao;

    @Generated(hash = 1293412156)
    private transient Long parent__resolvedKey;

    @Generated(hash = 368198959)
    public FavorRecordOrder(Long id, String name, String coverUrl, int number, int sortSeq,
            Date createTime, Date updateTime, long parentId) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
        this.number = number;
        this.sortSeq = sortSeq;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.parentId = parentId;
    }

    @Generated(hash = 368892490)
    public FavorRecordOrder() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSortSeq() {
        return this.sortSeq;
    }

    public void setSortSeq(int sortSeq) {
        this.sortSeq = sortSeq;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 402870660)
    public List<Record> getRecordList() {
        if (recordList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordDao targetDao = daoSession.getRecordDao();
            List<Record> recordListNew = targetDao
                    ._queryFavorRecordOrder_RecordList(id);
            synchronized (this) {
                if (recordList == null) {
                    recordList = recordListNew;
                }
            }
        }
        return recordList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1700181837)
    public synchronized void resetRecordList() {
        recordList = null;
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

    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 876689229)
    public FavorRecordOrder getParent() {
        long __key = this.parentId;
        if (parent__resolvedKey == null || !parent__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FavorRecordOrderDao targetDao = daoSession.getFavorRecordOrderDao();
            FavorRecordOrder parentNew = targetDao.load(__key);
            synchronized (this) {
                parent = parentNew;
                parent__resolvedKey = __key;
            }
        }
        return parent;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 791349808)
    public void setParent(@NotNull FavorRecordOrder parent) {
        if (parent == null) {
            throw new DaoException(
                    "To-one property 'parentId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.parent = parent;
            parentId = parent.getId();
            parent__resolvedKey = parentId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1096147098)
    public List<FavorRecordOrder> getChildList() {
        if (childList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FavorRecordOrderDao targetDao = daoSession.getFavorRecordOrderDao();
            List<FavorRecordOrder> childListNew = targetDao._queryFavorRecordOrder_ChildList(id);
            synchronized (this) {
                if (childList == null) {
                    childList = childListNew;
                }
            }
        }
        return childList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 20549044)
    public synchronized void resetChildList() {
        childList = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 760884618)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFavorRecordOrderDao() : null;
    }
}
