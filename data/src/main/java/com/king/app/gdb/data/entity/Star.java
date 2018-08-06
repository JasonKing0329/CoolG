package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:38
 */
@Entity(nameInDb = "stars")
public class Star {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private int records;
    private int betop;
    private int bebottom;
    private float average;
    private int max;
    private int min;
    private float caverage;
    private int cmax;
    private int cmin;

    private int favor;

    @ToMany(referencedJoinProperty = "starId")
    private List<StarRating> ratings;

    @ToMany
    @JoinEntity(
            entity = RecordStar.class,
            sourceProperty = "starId",
            targetProperty = "recordId"
    )
    private List<Record> recordList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1640983199)
    private transient StarDao myDao;

    @Generated(hash = 1604840945)
    public Star(Long id, String name, int records, int betop, int bebottom,
            float average, int max, int min, float caverage, int cmax, int cmin,
            int favor) {
        this.id = id;
        this.name = name;
        this.records = records;
        this.betop = betop;
        this.bebottom = bebottom;
        this.average = average;
        this.max = max;
        this.min = min;
        this.caverage = caverage;
        this.cmax = cmax;
        this.cmin = cmin;
        this.favor = favor;
    }

    @Generated(hash = 249476133)
    public Star() {
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

    public int getRecords() {
        return this.records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public int getBetop() {
        return this.betop;
    }

    public void setBetop(int betop) {
        this.betop = betop;
    }

    public int getBebottom() {
        return this.bebottom;
    }

    public void setBebottom(int bebottom) {
        this.bebottom = bebottom;
    }

    public float getAverage() {
        return this.average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public float getCaverage() {
        return this.caverage;
    }

    public void setCaverage(float caverage) {
        this.caverage = caverage;
    }

    public int getCmax() {
        return this.cmax;
    }

    public void setCmax(int cmax) {
        this.cmax = cmax;
    }

    public int getCmin() {
        return this.cmin;
    }

    public void setCmin(int cmin) {
        this.cmin = cmin;
    }

    public int getFavor() {
        return this.favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2122969233)
    public List<Record> getRecordList() {
        if (recordList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordDao targetDao = daoSession.getRecordDao();
            List<Record> recordListNew = targetDao._queryStar_RecordList(id);
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

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 467072215)
    public List<StarRating> getRatings() {
        if (ratings == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StarRatingDao targetDao = daoSession.getStarRatingDao();
            List<StarRating> ratingsNew = targetDao._queryStar_Ratings(id);
            synchronized (this) {
                if (ratings == null) {
                    ratings = ratingsNew;
                }
            }
        }
        return ratings;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1225759264)
    public synchronized void resetRatings() {
        ratings = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 832002360)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStarDao() : null;
    }
}
