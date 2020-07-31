package com.king.app.coolg.model.bean;

import org.greenrobot.greendao.Property;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/7/31 11:27
 */
public class StarSortBuilder {

    private Long tagId;

    private boolean isOrderByName;

    private boolean isOrderByRecords;

    private boolean isOrderByRandom;

    private Property orderByRatingProperty;

    public boolean isOrderByName() {
        return isOrderByName;
    }

    public StarSortBuilder setOrderByName(boolean orderByName) {
        isOrderByName = orderByName;
        return this;
    }

    public boolean isOrderByRecords() {
        return isOrderByRecords;
    }

    public StarSortBuilder setOrderByRecords(boolean orderByRecords) {
        isOrderByRecords = orderByRecords;
        return this;
    }

    public Long getTagId() {
        return tagId;
    }

    public StarSortBuilder setTagId(Long tagId) {
        this.tagId = tagId;
        return this;
    }

    public boolean isOrderByRandom() {
        return isOrderByRandom;
    }

    public StarSortBuilder setOrderByRandom(boolean orderByRandom) {
        isOrderByRandom = orderByRandom;
        return this;
    }

    public Property getOrderByRatingProperty() {
        return orderByRatingProperty;
    }

    public StarSortBuilder setOrderByRatingProperty(Property orderByRatingProperty) {
        this.orderByRatingProperty = orderByRatingProperty;
        return this;
    }
}
