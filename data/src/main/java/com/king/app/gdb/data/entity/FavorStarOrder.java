package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 16:59
 */
@Entity(tableName = "favor_order_star")
public class FavorStarOrder {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String name;

    private String coverUrl;

    private int number;

    private int sortSeq;

    private long parentId;

    private Date createTime;

    private Date updateTime;

    @Ignore
    private List<FavorStarOrder> childList;// FavDao.getChildRecordOrders(id)

    @Ignore
    private FavorStarOrder parent;// FavDao.getFavorRecordOrderByParent(parentId)

    @Ignore
    private List<Star> starList;// StarDao.getFavStars(id)

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
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<FavorStarOrder> getChildList() {
        return childList;
    }

    public void setChildList(List<FavorStarOrder> childList) {
        this.childList = childList;
    }

    public FavorStarOrder getParent() {
        return parent;
    }

    public void setParent(FavorStarOrder parent) {
        this.parent = parent;
    }

    public List<Star> getStarList() {
        return starList;
    }

    public void setStarList(List<Star> starList) {
        this.starList = starList;
    }
}
