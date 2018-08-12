package com.king.app.coolg.phone.star;

import com.king.app.gdb.data.entity.Star;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class StarRelationship {

    private Star star;

    private int count;

    private String imagePath;

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
