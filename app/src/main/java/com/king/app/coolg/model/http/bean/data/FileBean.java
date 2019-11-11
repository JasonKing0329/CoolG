package com.king.app.coolg.model.http.bean.data;

public class FileBean {

    private String name;

    private String extra;

    private String path;

    private boolean isFolder;

    private long lastModifyTime;

    private long size;

    /**
     * server端不赋值，client端记录parent
     */
    private FileBean parentBean;

    private String sourceUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public FileBean getParentBean() {
        return parentBean;
    }

    public void setParentBean(FileBean parentBean) {
        this.parentBean = parentBean;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
