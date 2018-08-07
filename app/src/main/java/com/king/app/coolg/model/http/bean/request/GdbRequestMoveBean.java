package com.king.app.coolg.model.http.bean.request;

import com.king.app.coolg.model.http.bean.data.DownloadItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/24 13:46
 */
public class GdbRequestMoveBean {
    private String type;
    private List<DownloadItem> downloadList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DownloadItem> getDownloadList() {
        return downloadList;
    }

    public void setDownloadList(List<DownloadItem> downloadList) {
        this.downloadList = downloadList;
    }
}
