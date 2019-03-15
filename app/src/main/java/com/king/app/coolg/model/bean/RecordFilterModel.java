package com.king.app.coolg.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

@Deprecated
public class RecordFilterModel {

    @SerializedName("list")
    private List<RecordFilterBean> list;

    private boolean isSupportNR;

    public List<RecordFilterBean> getList() {
        return list;
    }

    public void setList(List<RecordFilterBean> list) {
        this.list = list;
    }

    public boolean isSupportNR() {
        return isSupportNR;
    }

    public void setSupportNR(boolean supportNR) {
        isSupportNR = supportNR;
    }
}
