package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "star_category_details")
public class TopStar {

    @Id(autoincrement = true)
    private Long id;

    private long categoryId;

    private long starId;

    private int level;

    private int levelIndex;

    @Generated(hash = 1421095379)
    public TopStar(Long id, long categoryId, long starId, int level,
            int levelIndex) {
        this.id = id;
        this.categoryId = categoryId;
        this.starId = starId;
        this.level = level;
        this.levelIndex = levelIndex;
    }

    @Generated(hash = 334031470)
    public TopStar() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelIndex() {
        return this.levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }


}
