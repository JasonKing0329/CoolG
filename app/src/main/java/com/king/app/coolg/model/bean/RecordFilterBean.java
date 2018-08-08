package com.king.app.coolg.model.bean;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class RecordFilterBean {

    /**
     * 关键词名称
     */
    private String keyword;

    /**
     * 关键词在数据库中对应的字段
     */
    private String keywordFiled;

    private boolean isEnable;

    private int min;

    private int max;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordFiled() {
        return keywordFiled;
    }

    public void setKeywordFiled(String keywordFiled) {
        this.keywordFiled = keywordFiled;
    }
}
