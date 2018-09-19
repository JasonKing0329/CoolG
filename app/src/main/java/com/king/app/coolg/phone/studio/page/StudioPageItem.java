package com.king.app.coolg.phone.studio.page;

import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.FavorRecordOrder;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:30
 */
public class StudioPageItem {

    private String strCount;

    private String strHighCount;

    private String updateTime;

    private FavorRecordOrder order;

    private List<StarNumberItem> starList;

    private List<RecordProxy> recordList;

    public List<StarNumberItem> getStarList() {
        return starList;
    }

    public void setStarList(List<StarNumberItem> starList) {
        this.starList = starList;
    }

    public List<RecordProxy> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordProxy> recordList) {
        this.recordList = recordList;
    }

    public String getStrCount() {
        return strCount;
    }

    public void setStrCount(String strCount) {
        this.strCount = strCount;
    }

    public String getStrHighCount() {
        return strHighCount;
    }

    public void setStrHighCount(String strHighCount) {
        this.strHighCount = strHighCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public FavorRecordOrder getOrder() {
        return order;
    }

    public void setOrder(FavorRecordOrder order) {
        this.order = order;
    }
}
