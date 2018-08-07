package com.king.app.coolg.phone.download;

import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.model.http.bean.data.DownloadItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/25 11:50
 */
public interface IContentHolder extends IFragmentHolder {

    List<DownloadItem> getDownloadList();

    List<DownloadItem> getExistedList();

    void dismissDialog();

    void showListPage();

    void addDownloadItems(List<DownloadItem> checkedItems);

    DownloadViewModel getViewModel();
}
