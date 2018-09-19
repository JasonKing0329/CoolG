package com.king.app.coolg.phone.studio;

import com.king.app.gdb.data.entity.FavorRecordOrder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 10:49
 */
public class StudioSimpleItem {

    private String firstChar;

    private String name;

    private String number;

    private FavorRecordOrder order;

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public FavorRecordOrder getOrder() {
        return order;
    }

    public void setOrder(FavorRecordOrder order) {
        this.order = order;
    }
}
