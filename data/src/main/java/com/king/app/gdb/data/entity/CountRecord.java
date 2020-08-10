package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/10 8:54
 */
@Entity(nameInDb = "count_record")
public class CountRecord {
    @Id
    private Long recordId;

    private int rank;

    @Generated(hash = 811012284)
    public CountRecord(Long recordId, int rank) {
        this.recordId = recordId;
        this.rank = rank;
    }

    @Generated(hash = 1384947155)
    public CountRecord() {
    }

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
