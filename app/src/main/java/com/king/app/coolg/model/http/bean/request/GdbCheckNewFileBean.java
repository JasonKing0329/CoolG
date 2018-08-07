package com.king.app.coolg.model.http.bean.request;

import com.king.app.coolg.model.http.bean.data.DownloadItem;

import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class GdbCheckNewFileBean {
    private boolean isStarExisted;
    private boolean isRecordExisted;
    private List<DownloadItem> starItems;
    private List<DownloadItem> recordItems;

	public boolean isStarExisted() {
        return isStarExisted;
    }

    public void setStarExisted(boolean starExisted) {
        isStarExisted = starExisted;
    }

    public boolean isRecordExisted() {
        return isRecordExisted;
    }

    public void setRecordExisted(boolean recordExisted) {
        isRecordExisted = recordExisted;
    }

    public List<DownloadItem> getStarItems() {
        return starItems;
    }

    public void setStarItems(List<DownloadItem> starItems) {
        this.starItems = starItems;
    }

    public List<DownloadItem> getRecordItems() {
        return recordItems;
    }

    public void setRecordItems(List<DownloadItem> recordItems) {
        this.recordItems = recordItems;
    }
}
