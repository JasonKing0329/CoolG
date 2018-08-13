package com.king.app.coolg.phone.record.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/8/11 0011.
 */

public class RecordPhoneViewModel extends BaseViewModel {

    private String mScene;

    private RecordRepository repository;

    public MutableLiveData<List<Integer>> titlesObserver = new MutableLiveData<>();

    public RecordPhoneViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        mScene = AppConstants.KEY_SCENE_ALL;
    }

    public String getScene() {
        return mScene;
    }

    public void setScene(String mScene) {
        this.mScene = mScene;
    }

    public void loadTitles() {
        Observable.create((ObservableOnSubscribe<List<Integer>>) e -> {
            List<Integer> countList = new ArrayList<>();
            countList.add((int) repository.getRecordCount(0));
            countList.add((int) repository.getRecordCount(DataConstants.VALUE_RECORD_TYPE_1V1));
            countList.add((int) repository.getRecordCount(DataConstants.VALUE_RECORD_TYPE_3W));
            countList.add((int) repository.getRecordCount(DataConstants.VALUE_RECORD_TYPE_MULTI));
            countList.add((int) repository.getRecordCount(DataConstants.VALUE_RECORD_TYPE_LONG));
            e.onNext(countList);
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Integer> list) {
                        titlesObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load title error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
