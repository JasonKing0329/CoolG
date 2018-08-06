package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:43
 */
@Entity(nameInDb = "record_star")
public class RecordStar {

    @Id(autoincrement = true)
    private Long id;

    private long recordId;

    private long starId;

    private int type;

    private int score;

    private int scoreC;

    @ToOne(joinProperty = "starId")
    private Star star;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1671488507)
    private transient RecordStarDao myDao;

    @Generated(hash = 1052335727)
    public RecordStar(Long id, long recordId, long starId, int type, int score,
            int scoreC) {
        this.id = id;
        this.recordId = recordId;
        this.starId = starId;
        this.type = type;
        this.score = score;
        this.scoreC = scoreC;
    }

    @Generated(hash = 2109804186)
    public RecordStar() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreC() {
        return this.scoreC;
    }

    public void setScoreC(int scoreC) {
        this.scoreC = scoreC;
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
    @Generated(hash = 279875391)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecordStarDao() : null;
    }

}
