package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 10:40
 */
@Entity(tableName = "play_duration")
public class PlayDuration {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private long recordId;

    private int duration;

    private int total;

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

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
