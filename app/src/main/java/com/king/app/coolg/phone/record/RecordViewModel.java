package com.king.app.coolg.phone.record;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;

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
 * @date: 2018/8/8 13:24
 */
public class RecordViewModel extends BaseViewModel {

    public MutableLiveData<Record> recordObserver = new MutableLiveData<>();

    public MutableLiveData<String> singleImageObserver = new MutableLiveData<>();

    public MutableLiveData<List<String>> imagesObserver = new MutableLiveData<>();

    public MutableLiveData<List<RecordStar>> starsObserver = new MutableLiveData<>();

    private RecordRepository repository;

    private Record mRecord;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
    }

    public void loadRecord(long recordId) {
        repository.getRecord(recordId)
                .flatMap(record -> handleRecord(record))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Record>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Record record) {
                        mRecord = record;
                        recordObserver.setValue(record);
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

    private ObservableSource<Record> handleRecord(Record record) {
        return observer -> {

            // record images
            if (ImageProvider.hasRecordFolder(record.getName())) {
                List<String> list = ImageProvider.getRecordPathList(record.getName());
                if (list.size() > 1) {
                    imagesObserver.postValue(list);
                }
                else if (list.size() == 1) {
                    singleImageObserver.postValue(list.get(0));
                }
            }
            else {
                String path = ImageProvider.getRecordRandomPath(record.getName(), null);
                singleImageObserver.postValue(path);
            }

            // stars
            starsObserver.postValue(record.getRelationList());

            observer.onNext(record);
        };
    }
}
