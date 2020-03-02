package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "tag_star")
public class TagStar {

    @Id(autoincrement = true)
    private Long id;

    private long tagId;

    private long starId;

    @Generated(hash = 724935540)
    public TagStar(Long id, long tagId, long starId) {
        this.id = id;
        this.tagId = tagId;
        this.starId = starId;
    }

    @Generated(hash = 502219812)
    public TagStar() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }
}
