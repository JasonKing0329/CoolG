package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 9:51
 */
@Entity(tableName = "play_item")
public class PlayItem {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private long orderId;

    private long recordId;

    private String url;

    private int index;

    @Ignore
    private PlayOrder order;// PlayDao.getPlayOrder(orderId)

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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PlayOrder getOrder() {
        return order;
    }

    public void setOrder(PlayOrder order) {
        this.order = order;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
