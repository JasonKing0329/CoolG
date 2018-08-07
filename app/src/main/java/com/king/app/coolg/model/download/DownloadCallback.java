package com.king.app.coolg.model.download;

import com.king.app.coolg.model.http.bean.data.DownloadItem;

/**
 * Created by Administrator on 2016/9/2.
 */
public interface DownloadCallback {
    void onDownloadFinish(DownloadItem item);
    void onDownloadError(DownloadItem item);
    void onDownloadAllFinish();
}
