package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/10 8:54
 */
@Entity(tableName = "count_star")
public class CountStar {

    @PrimaryKey
    private Long starId;

    private int rank;

    public Long getStarId() {
        return this.starId;
    }

    public void setStarId(Long starId) {
        this.starId = starId;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
