package com.king.app.coolg.phone.studio;

import com.king.app.gdb.data.entity.FavorRecordOrder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 11:04
 */
public class StudioRichItem {

    private String imageUrl;

    private String name;

    private String count;

    private String high;

    private FavorRecordOrder order;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public FavorRecordOrder getOrder() {
        return order;
    }

    public void setOrder(FavorRecordOrder order) {
        this.order = order;
    }
}
