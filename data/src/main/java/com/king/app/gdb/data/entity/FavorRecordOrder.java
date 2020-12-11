package com.king.app.gdb.data.entity;

import android.arch.persistence.room.ColumnInfo;
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
@Entity(tableName = "favor_order_record")
public class FavorRecordOrder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private Long id;

    private String name;

    @ColumnInfo(name = "cover_url")
    private String coverUrl;

    private int number;

    @ColumnInfo(name = "sort_seq")
    private int sortSeq;

    @ColumnInfo(name = "parent_id")
    private long parentId;

    @ColumnInfo(name = "create_time")
    private Date createTime;

    @ColumnInfo(name = "update_time")
    private Date updateTime;

    @Ignore
    private List<FavorRecordOrder> childList;// FavDao.getChildRecordOrders(id)

    @Ignore
    private FavorRecordOrder parent;// FavDao.getFavorRecordOrderByParent(parentId)

    @Ignore
    private List<Record> recordList;// RecordDao.getFavRecords(id)

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
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<FavorRecordOrder> getChildList() {
        return childList;
    }

    public void setChildList(List<FavorRecordOrder> childList) {
        this.childList = childList;
    }

    public FavorRecordOrder getParent() {
        return parent;
    }

    public void setParent(FavorRecordOrder parent) {
        this.parent = parent;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
