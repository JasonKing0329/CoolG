package com.king.app.coolg.pad.record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/12 10:57
 */
public class TitleValueBean {

    private boolean isOnlyValue;

    private String title;

    private String value;

    public boolean isOnlyValue() {
        return isOnlyValue;
    }

    public void setOnlyValue(boolean onlyValue) {
        isOnlyValue = onlyValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
