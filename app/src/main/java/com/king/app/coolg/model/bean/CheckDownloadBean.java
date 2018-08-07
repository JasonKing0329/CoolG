package com.king.app.coolg.model.bean;

import com.king.app.coolg.model.http.bean.data.DownloadItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/23 9:45
 */
public class CheckDownloadBean {
    private boolean hasNew;
    private List<DownloadItem> downloadList;
    private List<DownloadItem> repeatList;
    private String targetPath;

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public List<DownloadItem> getDownloadList() {
        return downloadList;
    }

    public void setDownloadList(List<DownloadItem> downloadList) {
        this.downloadList = downloadList;
    }

    public List<DownloadItem> getRepeatList() {
        return repeatList;
    }

    public void setRepeatList(List<DownloadItem> repeatList) {
        this.repeatList = repeatList;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
