package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "star_category")
public class TopStarCategory {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private int index;

    private int type;

    private int number;

    @Generated(hash = 847916713)
    public TopStarCategory(Long id, String name, int index, int type, int number) {
        this.id = id;
        this.name = name;
        this.index = index;
        this.type = type;
        this.number = number;
    }

    @Generated(hash = 1438005522)
    public TopStarCategory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
}
