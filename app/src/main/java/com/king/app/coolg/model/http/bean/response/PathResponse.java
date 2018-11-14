package com.king.app.coolg.model.http.bean.response;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/14 13:50
 */
public class PathResponse {

    /**
     * 格式如
     * folder/.../XXX.mp4
     * 拼接在BaseHttpClient.getBaseUrl()之后
     */
    private String path;

    private boolean isAvailable;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
