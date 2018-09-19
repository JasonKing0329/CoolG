package com.king.app.coolg.phone.studio.page;

import com.king.app.gdb.data.entity.Star;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 16:20
 */
public class StarNumberItem {
    private String name;
    private String imageUrl;
    private int number;
    private Star star;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }
}
