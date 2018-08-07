package com.king.app.coolg.model.http.bean.response;

import com.google.gson.annotations.SerializedName;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/24 13:49
 */
public class GdbMoveResponse {

    @SerializedName("isSuccess")
    private boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
