package com.king.app.coolg.phone.video.home;

import com.king.app.gdb.data.entity.Record;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:24
 */
public class VideoItem {

    private Record record;

    private String playUrl;

    private String coverUrl;

    private String name;

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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
