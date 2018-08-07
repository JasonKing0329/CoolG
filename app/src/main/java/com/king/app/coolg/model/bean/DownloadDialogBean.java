package com.king.app.coolg.model.bean;

import com.king.app.coolg.model.http.bean.data.DownloadItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/24 17:31
 */
public class DownloadDialogBean {

    /**
     * 本地不存在的待下载内容
     */
    private List<DownloadItem> downloadList;

    /**
     * 本地已存在的待下载任务
     */
    private List<DownloadItem> existedList;

    /**
     * 下载目录
     */
    private String savePath;

    /**
     * 直接下载，不提示
     */
    private boolean isShowPreview;

    public List<DownloadItem> getDownloadList() {
        return downloadList;
    }

    public void setDownloadList(List<DownloadItem> downloadList) {
        this.downloadList = downloadList;
    }

    public List<DownloadItem> getExistedList() {
        return existedList;
    }

    public void setExistedList(List<DownloadItem> existedList) {
        this.existedList = existedList;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public boolean isShowPreview() {
        return isShowPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.isShowPreview = showPreview;
    }

}
