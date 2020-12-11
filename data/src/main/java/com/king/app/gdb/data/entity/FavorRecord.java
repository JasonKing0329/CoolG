package com.king.app.gdb.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/13 17:06
 */
@Entity(tableName = "favor_record")
public class FavorRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private Long id;

    @ColumnInfo(name = "order_id")
    private long orderId;

    @ColumnInfo(name = "record_id")
    private long recordId;

    @ColumnInfo(name = "create_time")
    private Date createTime;

    @ColumnInfo(name = "update_time")
    private Date updateTime;

    @Ignore
    private FavorRecordOrder order;// FaveDao.getFavorRecordOrder(orderId)

    @Ignore
    private Record record;// RecordDao.getRecord(recordId)

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

    public FavorRecordOrder getOrder() {
        return order;
    }

    public void setOrder(FavorRecordOrder order) {
        this.order = order;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
