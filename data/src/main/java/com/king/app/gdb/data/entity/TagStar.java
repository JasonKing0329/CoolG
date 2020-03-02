package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(nameInDb = "tag_star")
public class TagStar {

    @Id(autoincrement = true)
    private Long id;

    private long tagId;

    private long starId;

    @ToOne(joinProperty = "tagId")
    private Tag tag;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1099221066)
    private transient TagStarDao myDao;

    @Generated(hash = 1006483784)
    private transient Long tag__resolvedKey;

    @Generated(hash = 724935540)
    public TagStar(Long id, long tagId, long starId) {
        this.id = id;
        this.tagId = tagId;
        this.starId = starId;
    }

    @Generated(hash = 502219812)
    public TagStar() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1607739560)
    public Tag getTag() {
        long __key = this.tagId;
        if (tag__resolvedKey == null || !tag__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            Tag tagNew = targetDao.load(__key);
            synchronized (this) {
                tag = tagNew;
                tag__resolvedKey = __key;
            }
        }
        return tag;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1728988431)
    public void setTag(@NotNull Tag tag) {
        if (tag == null) {
            throw new DaoException(
                    "To-one property 'tagId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.tag = tag;
            tagId = tag.getId();
            tag__resolvedKey = tagId;
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
    @Generated(hash = 579934500)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTagStarDao() : null;
    }
}
