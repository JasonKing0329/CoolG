package com.king.app.coolg.phone.record;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.io.File;
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
 * @date: 2018/8/8 13:24
 */
public class RecordViewModel extends BaseViewModel {

    public MutableLiveData<Record> recordObserver = new MutableLiveData<>();

    public MutableLiveData<String> singleImageObserver = new MutableLiveData<>();

    public MutableLiveData<List<String>> imagesObserver = new MutableLiveData<>();

    public MutableLiveData<List<RecordStar>> starsObserver = new MutableLiveData<>();

    public MutableLiveData<List<PassionPoint>> passionsObserver = new MutableLiveData<>();

    public MutableLiveData<List<FavorRecordOrder>> ordersObserver = new MutableLiveData<>();

    private RecordRepository repository;
    private OrderRepository orderRepository;

    protected Record mRecord;

    private String mSingleImagePath;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        orderRepository = new OrderRepository();
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

    private void loadImages(Record record) {
        if (ImageProvider.hasRecordFolder(record.getName())) {
            List<String> list = ImageProvider.getRecordPathList(record.getName());
            if (list.size() > 1) {
                imagesObserver.postValue(list);
            }
            else if (list.size() == 1) {
                mSingleImagePath = list.get(0);
                singleImageObserver.postValue(list.get(0));
            }
        }
        else {
            String path = ImageProvider.getRecordRandomPath(record.getName(), null);
            mSingleImagePath = path;
            singleImageObserver.postValue(path);
        }
    }

    public String getSingleImagePath() {
        return mSingleImagePath;
    }

    private ObservableSource<Record> handleRecord(Record record) {
        return observer -> {

            // record images
            loadImages(record);

            // stars
            starsObserver.postValue(record.getRelationList());

            // passion point
            passionsObserver.postValue(getPassions(record));

            observer.onNext(record);
        };
    }

    protected List<PassionPoint> getPassions(Record record) {
        List<PassionPoint> pointList = new ArrayList<>();
        if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            getPassionList(pointList, record.getRecordType1v1());
        }
        else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
            getPassionList(pointList, record.getRecordType3w());
        }
        return pointList;
    }

    private void getPassionList(List<PassionPoint> pointList, RecordType3w record) {

        if (record.getScoreFkType1() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("For Sit");
            point.setContent(record.getScoreFkType1() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType2() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Back Sit");
            point.setContent(record.getScoreFkType2() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType3() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("For");
            point.setContent(record.getScoreFkType3() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType4() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Back");
            point.setContent(record.getScoreFkType4() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType5() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Side");
            point.setContent(record.getScoreFkType5() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType6() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Double");
            point.setContent(record.getScoreFkType6() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType7() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Sequence");
            point.setContent(record.getScoreFkType7() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType8() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Special");
            point.setContent(record.getScoreFkType8() + "");
            pointList.add(point);
        }
    }

    private void getPassionList(List<PassionPoint> pointList, RecordType1v1 record) {
        if (record.getScoreFkType1() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("For Sit");
            point.setContent(record.getScoreFkType1() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType2() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Back Sit");
            point.setContent(record.getScoreFkType2() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType3() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("For Stand");
            point.setContent(record.getScoreFkType3() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType4() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Back Stand");
            point.setContent(record.getScoreFkType4() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType5() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Side");
            point.setContent(record.getScoreFkType5() + "");
            pointList.add(point);
        }
        if (record.getScoreFkType6() > 0) {
            PassionPoint point = new PassionPoint();
            point.setKey("Special");
            point.setContent(record.getScoreFkType6() + "");
            pointList.add(point);
        }
    }

    public void loadRecordOrders() {
        orderRepository.getRecordOrders(mRecord.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<FavorRecordOrder>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<FavorRecordOrder> list) {
                        ordersObserver.setValue(list);
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

    public void addToOrder(long orderId) {
        orderRepository.addFavorRecord(orderId, mRecord.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<FavorRecord>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(FavorRecord favorStar) {
                        messageObserver.setValue("Add successfully");
                        loadRecordOrders();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteImage(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        loadImages(mRecord);
    }
}
