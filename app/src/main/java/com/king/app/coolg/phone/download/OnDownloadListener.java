package com.king.app.coolg.phone.download;

import com.king.app.coolg.model.http.bean.data.DownloadItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 13:54
 */
public interface OnDownloadListener {
    void onDownloadFinish(DownloadItem item);

    void onDownloadFinish();
}
