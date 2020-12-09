package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 9:57
 */
@Entity(tableName = "play_order")
public class PlayOrder {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String name;

    private String coverUrl;

    @Ignore
    private List<PlayItem> list;// PlayDao.getPlayItems(id)

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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<PlayItem> getList() {
        return list;
    }

    public void setList(List<PlayItem> list) {
        this.list = list;
    }
}
