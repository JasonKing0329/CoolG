package com.king.app.coolg.phone.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 14:41
 */
public class HomeViewModel extends BaseViewModel {

    private final int LOAD_NUM = 20;

    private int mOffset;

    private RecordRepository recordRepository;

    private HomeBean mHomeBean;

    public MutableLiveData<HomeBean> homeObserver = new MutableLiveData<>();

    public MutableLiveData<Integer> newRecordsObserver = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        recordRepository = new RecordRepository();
        mHomeBean = new HomeBean();
        mHomeBean.setRecordList(new ArrayList<>());
    }

    public void loadData() {
        mOffset = 0;

        loadingObserver.setValue(true);
        recordRepository.getLatestRecords(mOffset, LOAD_NUM)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        loadingObserver.setValue(false);
                        mOffset += records.size();

                        mHomeBean.setRecordList(records);
                        homeObserver.setValue(mHomeBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadMore() {
        recordRepository.getLatestRecords(mOffset, LOAD_NUM)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        mHomeBean.getRecordList().addAll(records);
                        mOffset += records.size();

                        newRecordsObserver.setValue(records.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
