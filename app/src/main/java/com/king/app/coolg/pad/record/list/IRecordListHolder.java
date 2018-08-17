package com.king.app.coolg.pad.record.list;

import android.view.View;

import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.gdb.data.entity.Record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 10:39
 */
public interface IRecordListHolder extends IFragmentHolder {

    void showRecordPopup(View v, Record record);
}
