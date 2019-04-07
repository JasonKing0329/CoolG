package com.king.app.coolg.model.bean;

public class TitleValueBean {

    private String title;

    private String value;

    public TitleValueBean() {

    }

    public TitleValueBean(String title, String value) {
        this.title = title;
        this.value = value;
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
