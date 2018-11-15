package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 10:40
 */
@Entity(nameInDb = "play_duration")
public class PlayDuration {

    @Id(autoincrement = true)
    private Long id;

    private long recordId;

    private int duration;

    private int total;

    @Generated(hash = 1743849621)
    public PlayDuration(Long id, long recordId, int duration, int total) {
        this.id = id;
        this.recordId = recordId;
        this.duration = duration;
        this.total = total;
    }

    @Generated(hash = 1160334389)
    public PlayDuration() {
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
