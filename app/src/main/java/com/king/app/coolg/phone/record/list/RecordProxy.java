package com.king.app.coolg.phone.record.list;

import com.king.app.gdb.data.entity.Record;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:01
 */
public class RecordProxy {
    private Record record;
    private String imagePath;
    private int offsetIndex;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getOffsetIndex() {
        return offsetIndex;
    }

    public void setOffsetIndex(int offsetIndex) {
        this.offsetIndex = offsetIndex;
    }
}
