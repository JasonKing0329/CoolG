package com.king.app.coolg.model.bean;

import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.gdb.data.RecordCursor;

/**
 * Created by Administrator on 2018/8/11 0011.
 */

public class RecordComplexFilter {

    private int sortType;
    // 固定recordType
    private Integer recordType;
    private boolean desc;
    private String nameLike;
    private String scene;
    private RecordCursor cursor;
    private RecommendBean filter;
    private long starId;
    private long studioId;

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public boolean getDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public RecordCursor getCursor() {
        return cursor;
    }

    public void setCursor(RecordCursor cursor) {
        this.cursor = cursor;
    }

    public RecommendBean getFilter() {
        return filter;
    }

    public void setFilter(RecommendBean filter) {
        this.filter = filter;
    }

    public long getStarId() {
        return starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public long getStudioId() {
        return studioId;
    }

    public void setStudioId(long studioId) {
        this.studioId = studioId;
    }
}
