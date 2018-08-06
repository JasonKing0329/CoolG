package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:43
 */
@Entity(nameInDb = "properties")
public class GProperties {

    @Id(autoincrement = true)
    private Long id;

    private String key;

    private String value;

    private String other;

    @Generated(hash = 1904526321)
    public GProperties(Long id, String key, String value, String other) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.other = other;
    }

    @Generated(hash = 562909354)
    public GProperties() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOther() {
        return this.other;
    }

    public void setOther(String other) {
        this.other = other;
    }

}
