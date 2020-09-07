package com.king.app.coolg.phone.star.random;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/7 11:16
 */
public class RandomRule {

    private boolean isExcludeFromMarked;

    /**
     * 0 all, 1 top, 2 bottom, 3 half
     */
    private int starType;

    private String sqlRating;

    public boolean isExcludeFromMarked() {
        return isExcludeFromMarked;
    }

    public void setExcludeFromMarked(boolean excludeFromMarked) {
        isExcludeFromMarked = excludeFromMarked;
    }

    public int getStarType() {
        return starType;
    }

    public void setStarType(int starType) {
        this.starType = starType;
    }

    public String getSqlRating() {
        return sqlRating;
    }

    public void setSqlRating(String sqlRating) {
        this.sqlRating = sqlRating;
    }
}
