package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 16:59
 */
@Entity(nameInDb = "favor_order_star")
public class FavorStarOrder {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private String coverUrl;

    private int number;

    private int sortSeq;

    @ToMany
    @JoinEntity(
            entity = FavorStar.class,
            sourceProperty = "orderId",
            targetProperty = "starId"
    )
    private List<Star> starList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1378136452)
    private transient FavorStarOrderDao myDao;

    @Generated(hash = 1920745159)
    public FavorStarOrder(Long id, String name, String coverUrl, int number,
            int sortSeq) {
        this.id = id;
        this.name = name;
        this.coverUrl = coverUrl;
        this.number = number;
        this.sortSeq = sortSeq;
    }

    @Generated(hash = 2127781905)
    public FavorStarOrder() {
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
    @Generated(hash = 1042595241)
    public List<Star> getStarList() {
        if (starList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StarDao targetDao = daoSession.getStarDao();
            List<Star> starListNew = targetDao._queryFavorStarOrder_StarList(id);
            synchronized (this) {
                if (starList == null) {
                    starList = starListNew;
                }
            }
        }
        return starList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1358641491)
    public synchronized void resetStarList() {
        starList = null;
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
    @Generated(hash = 1514919729)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFavorStarOrderDao() : null;
    }
}
