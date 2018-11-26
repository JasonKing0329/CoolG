package com.king.app.coolg.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.FileUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: 支持start和bind方式
 * start方式直接后台运行
 * bind方式提供回调
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/23 10:35
 */
public class FileService extends Service {

    private Disposable disposable;
    private Map<String, Boolean> useMap;

    private IFileServiceCallback serviceCallback;

    private boolean isWorking;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DebugLog.e("");
        return new FileBinder();
    }

    public  class FileBinder extends Binder {

        public FileService getService() {
            return FileService.this;
        }

        public void setServiceCallback(IFileServiceCallback serviceCallback) {
            FileService.this.serviceCallback = serviceCallback;
        }

        public void removeServiceCallback() {
            FileService.this.serviceCallback = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.e("");
        if (!isWorking) {
            isWorking = true;
            removeUselessFiles();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void removeUselessFiles() {
        DebugLog.e("");
        disposable = Observable.create(e -> {
            removeUselessRecords();
            removeUselessStars();
            e.onNext(new Object());
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {
                    DebugLog.e("finished");
                    isWorking = false;
                    if (serviceCallback != null) {
                        serviceCallback.onClearFinished();
                    }
                }, throwable -> throwable.printStackTrace());
    }

    private void removeUselessRecords() {
        File file = new File(AppConfig.GDB_IMG_RECORD);
        RecordDao dao = CoolApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> list = dao.queryBuilder().build().list();
        useMap = new HashMap<>();
        for (Record record:list) {
            useMap.put(record.getName(), true);
        }
        File files[] = file.listFiles();
        for (File f:files) {
            removeFiles(f);
        }
    }

    private void removeUselessStars() {
        File file = new File(AppConfig.GDB_IMG_STAR);
        StarDao dao = CoolApplication.getInstance().getDaoSession().getStarDao();
        List<Star> list = dao.queryBuilder().build().list();
        useMap = new HashMap<>();
        for (Star star:list) {
            useMap.put(star.getName(), true);
        }
        File files[] = file.listFiles();
        for (File f:files) {
            removeFiles(f);
        }
    }

    /**
     * 文件层级结构类型如下
     * 1. 直接以 key.图片格式 命名的文件
     * 2. 以key命名的目录
     * @param file
     */
    private void removeFiles(File file) {
        if (file.isDirectory()) {
            if (useMap.get(file.getName()) == null) {
                DebugLog.e(file.getPath());
                FileUtil.deleteFile(file);
            }
        }
        else {
            if (file.getName().equals(".nomedia")) {
                return;
            }
            // 数据库里没有相关引用就删除
            if (file.getName().contains(".")) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("."));
                if (useMap.get(name) == null) {
                    DebugLog.e(file.getPath());
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        DebugLog.e("");
        serviceCallback = null;
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
