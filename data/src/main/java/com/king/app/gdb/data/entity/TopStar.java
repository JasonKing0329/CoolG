package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(nameInDb = "star_category_details")
public class TopStar {

    @Id(autoincrement = true)
    private Long id;

    private long categoryId;

    private long starId;

    private int level;

    private int levelIndex;

    @ToOne(joinProperty = "starId")
    private Star star;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 657160725)
    private transient TopStarDao myDao;

    @Generated(hash = 758316439)
    private transient Long star__resolvedKey;

    @Generated(hash = 1421095379)
    public TopStar(Long id, long categoryId, long starId, int level,
            int levelIndex) {
        this.id = id;
        this.categoryId = categoryId;
        this.starId = starId;
        this.level = level;
        this.levelIndex = levelIndex;
    }

    @Generated(hash = 334031470)
    public TopStar() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelIndex() {
        return this.levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

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
    @Generated(hash = 411010189)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTopStarDao() : null;
    }


}
