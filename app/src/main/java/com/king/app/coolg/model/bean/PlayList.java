package com.king.app.coolg.model.bean;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/27 10:44
 */
public class PlayList {

    private List<PlayItem> list;

    private int playIndex;

    /**
     * 0 顺序；1 随机
     */
    private int playMode;

    public List<PlayItem> getList() {
        return list;
    }

    public void setList(List<PlayItem> list) {
        this.list = list;
    }

    public int getPlayIndex() {
        return playIndex;
    }

    public void setPlayIndex(int playIndex) {
        this.playIndex = playIndex;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public static class PlayItem {
        private String url;
        private String name;
        private int playTime;
        private int index;
        private long recordId;
        private int duration;

        // 扩展字段，不进行持久化存储
        private transient String imageUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getPlayTime() {
            return playTime;
        }

        public void setPlayTime(int playTime) {
            this.playTime = playTime;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public long getRecordId() {
            return recordId;
        }

        public void setRecordId(long recordId) {
            this.recordId = recordId;
        }
    }
}
