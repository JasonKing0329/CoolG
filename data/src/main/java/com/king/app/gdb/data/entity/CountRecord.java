package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/10 8:54
 */
@Entity(tableName = "count_record")
public class CountRecord {
    @PrimaryKey
    private Long recordId;

    private int rank;

    public Long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
