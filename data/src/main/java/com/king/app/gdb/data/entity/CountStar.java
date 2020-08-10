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
@Entity(nameInDb = "count_star")
public class CountStar {

    @Id
    private Long starId;

    private int rank;

    @Generated(hash = 1258712120)
    public CountStar(Long starId, int rank) {
        this.starId = starId;
        this.rank = rank;
    }

    @Generated(hash = 1127431462)
    public CountStar() {
    }

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
