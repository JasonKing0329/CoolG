package com.king.app.coolg.phone.record.manage;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/11/18 14:16
 */
public class NoStudioViewModel extends BaseViewModel {

    public MutableLiveData<List<RecordProxy>> listObserver = new MutableLiveData<>();

    private RecordRepository repository;

    public NoStudioViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
    }

    public void loadData() {
        loadingObserver.setValue(true);
        repository.getRecordsWithoutStudio()
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> list) {
                        loadingObserver.setValue(false);
                        listObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private ObservableSource<List<RecordProxy>> toViewItems(List<Record> list) {
        return observer -> {
            List<RecordProxy> results = new ArrayList<>();
            for (Record record:list) {
                RecordProxy proxy = new RecordProxy();
                proxy.setRecord(record);
                proxy.setImagePath(ImageProvider.getRecordRandomPath(record.getName(), null));
                results.add(proxy);
            }
            observer.onNext(results);
        };
    }

}
