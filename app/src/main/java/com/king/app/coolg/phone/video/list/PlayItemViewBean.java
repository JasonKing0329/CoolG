package com.king.app.coolg.phone.video.list;

import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.Record;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 14:36
 */
public class PlayItemViewBean {

    private PlayItem playItem;

    private String cover;

    private Record record;

    private String playUrl;

    private String name;

    public PlayItem getPlayItem() {
        return playItem;
    }

    public void setPlayItem(PlayItem playItem) {
        this.playItem = playItem;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
