package com.king.app.coolg.phone.download;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.bean.DownloadItemProxy;
import com.king.app.coolg.model.download.DownloadCallback;
import com.king.app.coolg.model.download.DownloadManager;
import com.king.app.coolg.model.http.bean.data.DownloadItem;
import com.king.app.coolg.model.http.progress.ProgressListener;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 13:40
 */
public class DownloadViewModel extends BaseViewModel {

    private DownloadManager downloadManager;

    private List<DownloadItemProxy> itemList;

    public MutableLiveData<List<DownloadItemProxy>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<Integer> progressObserver = new MutableLiveData<>();

    private OnDownloadListener onDownloadListener;

    public DownloadViewModel(@NonNull Application application) {
        super(application);
        downloadManager = new DownloadManager(downloadCallback, PreferenceValue.HTTP_MAX_DOWNLOAD);
    }

    public void setSavePath(String path) {
        downloadManager.setSavePath(path);
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    private DownloadCallback downloadCallback = new DownloadCallback() {
        @Override
        public void onDownloadFinish(DownloadItem item) {
            if (onDownloadListener != null) {
                onDownloadListener.onDownloadFinish(item);
            }
        }

        @Override
        public void onDownloadError(DownloadItem item) {
            DebugLog.e(item.getName());
            messageObserver.setValue("Error: " + item.getName());
        }

        @Override
        public void onDownloadAllFinish() {
            if (onDownloadListener != null) {
                onDownloadListener.onDownloadFinish();
            }
        }
    };

    public void initDownloadItems(List<DownloadItem> list) {
        itemList = new ArrayList<>();
        for (DownloadItem item:list) {
            DownloadItemProxy proxy = new DownloadItemProxy();
            proxy.setItem(item);
            proxy.setProgress(0);
            itemList.add(proxy);
        }
        itemsObserver.setValue(itemList);
    }

    public void startDownload() {
        for (int i = 0; i < itemList.size(); i ++) {
            final int index = i;
            downloadManager.downloadFile(itemList.get(i).getItem(), new ProgressListener() {
                private int lastProgress;
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    int progress = (int)(100 * 1f * bytesRead / contentLength);
//                    DebugLog.e("progress:" + progress);

                    if (progress - lastProgress > 8 || done) {// 避免更新太过频繁
                        lastProgress = progress;
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        bundle.putInt("progress", progress);
                        Message message = new Message();
                        message.setData(bundle);
                        uiHandler.sendMessage(message);
                    }
                }
            });
        }
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int index = bundle.getInt("index");
            int progress = bundle.getInt("progress");
            itemList.get(index).setProgress(progress);

            progressObserver.setValue(index);
        }
    };

}
