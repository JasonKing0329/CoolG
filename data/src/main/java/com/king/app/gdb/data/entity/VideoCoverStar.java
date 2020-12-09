package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 17:31
 */
@Entity(tableName = "video_cover_star")
public class VideoCoverStar {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private long starId;

    @Ignore
    private Star star;// StarDao.getStar(starId)

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }
}
