package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:46
 */
@Entity(nameInDb = "record")
public class Record {

    @Id(autoincrement = true)
    private Long id;

    private String scene;
    private String directory;
    private String name;
    private int hdLevel;
    private int score;
    private int scoreFeel;
    private int scoreStar;
    private int scorePassion;
    private int scoreCum;
    private int scoreSpecial;
    private int scoreBareback;
    private int deprecated;
    private String specialDesc;
    private long lastModifyTime;
    private int type;
    private long recordDetailId;

    @ToOne(joinProperty = "recordDetailId")
    private RecordType1v1 recordType1v1;

    @ToOne(joinProperty = "recordDetailId")
    private RecordType3w recordType3w;

    @ToMany(referencedJoinProperty = "recordId")
    private List<RecordStar> relationList;

    @ToMany
    @JoinEntity(
            entity = RecordStar.class,
            sourceProperty = "recordId",
            targetProperty = "starId"
    )
    private List<Star> starList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 765166123)
    private transient RecordDao myDao;

    @Generated(hash = 1313977001)
    public Record(Long id, String scene, String directory, String name, int hdLevel,
            int score, int scoreFeel, int scoreStar, int scorePassion, int scoreCum,
            int scoreSpecial, int scoreBareback, int deprecated, String specialDesc,
            long lastModifyTime, int type, long recordDetailId) {
        this.id = id;
        this.scene = scene;
        this.directory = directory;
        this.name = name;
        this.hdLevel = hdLevel;
        this.score = score;
        this.scoreFeel = scoreFeel;
        this.scoreStar = scoreStar;
        this.scorePassion = scorePassion;
        this.scoreCum = scoreCum;
        this.scoreSpecial = scoreSpecial;
        this.scoreBareback = scoreBareback;
        this.deprecated = deprecated;
        this.specialDesc = specialDesc;
        this.lastModifyTime = lastModifyTime;
        this.type = type;
        this.recordDetailId = recordDetailId;
    }

    @Generated(hash = 477726293)
    public Record() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScene() {
        return this.scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHdLevel() {
        return this.hdLevel;
    }

    public void setHdLevel(int hdLevel) {
        this.hdLevel = hdLevel;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreFeel() {
        return this.scoreFeel;
    }

    public void setScoreFeel(int scoreFeel) {
        this.scoreFeel = scoreFeel;
    }

    public int getScoreStar() {
        return this.scoreStar;
    }

    public void setScoreStar(int scoreStar) {
        this.scoreStar = scoreStar;
    }

    public int getScorePassion() {
        return this.scorePassion;
    }

    public void setScorePassion(int scorePassion) {
        this.scorePassion = scorePassion;
    }

    public int getScoreCum() {
        return this.scoreCum;
    }

    public void setScoreCum(int scoreCum) {
        this.scoreCum = scoreCum;
    }

    public int getScoreSpecial() {
        return this.scoreSpecial;
    }

    public void setScoreSpecial(int scoreSpecial) {
        this.scoreSpecial = scoreSpecial;
    }

    public int getScoreBareback() {
        return this.scoreBareback;
    }

    public void setScoreBareback(int scoreBareback) {
        this.scoreBareback = scoreBareback;
    }

    public int getDeprecated() {
        return this.deprecated;
    }

    public void setDeprecated(int deprecated) {
        this.deprecated = deprecated;
    }

    public String getSpecialDesc() {
        return this.specialDesc;
    }

    public void setSpecialDesc(String specialDesc) {
        this.specialDesc = specialDesc;
    }

    public long getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getRecordDetailId() {
        return this.recordDetailId;
    }

    public void setRecordDetailId(long recordDetailId) {
        this.recordDetailId = recordDetailId;
    }

    @Generated(hash = 1847110640)
    private transient Long recordType1v1__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 363482163)
    public RecordType1v1 getRecordType1v1() {
        long __key = this.recordDetailId;
        if (recordType1v1__resolvedKey == null
                || !recordType1v1__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordType1v1Dao targetDao = daoSession.getRecordType1v1Dao();
            RecordType1v1 recordType1v1New = targetDao.load(__key);
            synchronized (this) {
                recordType1v1 = recordType1v1New;
                recordType1v1__resolvedKey = __key;
            }
        }
        return recordType1v1;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1956655918)
    public void setRecordType1v1(@NotNull RecordType1v1 recordType1v1) {
        if (recordType1v1 == null) {
            throw new DaoException(
                    "To-one property 'recordDetailId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.recordType1v1 = recordType1v1;
            recordDetailId = recordType1v1.getId();
            recordType1v1__resolvedKey = recordDetailId;
        }
    }

    @Generated(hash = 405205600)
    private transient Long recordType3w__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1781989542)
    public RecordType3w getRecordType3w() {
        long __key = this.recordDetailId;
        if (recordType3w__resolvedKey == null
                || !recordType3w__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordType3wDao targetDao = daoSession.getRecordType3wDao();
            RecordType3w recordType3wNew = targetDao.load(__key);
            synchronized (this) {
                recordType3w = recordType3wNew;
                recordType3w__resolvedKey = __key;
            }
        }
        return recordType3w;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 122607578)
    public void setRecordType3w(@NotNull RecordType3w recordType3w) {
        if (recordType3w == null) {
            throw new DaoException(
                    "To-one property 'recordDetailId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.recordType3w = recordType3w;
            recordDetailId = recordType3w.getId();
            recordType3w__resolvedKey = recordDetailId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1671217317)
    public List<RecordStar> getRelationList() {
        if (relationList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordStarDao targetDao = daoSession.getRecordStarDao();
            List<RecordStar> relationListNew = targetDao
                    ._queryRecord_RelationList(id);
            synchronized (this) {
                if (relationList == null) {
                    relationList = relationListNew;
                }
            }
        }
        return relationList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 263067720)
    public synchronized void resetRelationList() {
        relationList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2061835819)
    public List<Star> getStarList() {
        if (starList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StarDao targetDao = daoSession.getStarDao();
            List<Star> starListNew = targetDao._queryRecord_StarList(id);
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
    @Generated(hash = 1505145191)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecordDao() : null;
    }

}
