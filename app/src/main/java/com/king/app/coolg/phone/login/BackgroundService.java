package com.king.app.coolg.phone.login;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.utils.DebugLog;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * createNoMedia cost too much time, run it in service
 * Created by Administrator on 2018/8/26 0026.
 */

public class BackgroundService extends Service {

    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        compositeDisposable = new CompositeDisposable();
        createNoMedia()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Object o) {
                        DebugLog.e("Background service end");
                        stopSelf();
                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugLog.e("Background service error");
                        e.printStackTrace();
                        stopSelf();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    private Observable<Object> createNoMedia() {
        DebugLog.e("");
        return Observable.create(e -> {
            AppConfig.createNoMedia();
            e.onNext(new Object());
        });
    }

    @Override
    public void onDestroy() {
        DebugLog.e("");
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }
}
