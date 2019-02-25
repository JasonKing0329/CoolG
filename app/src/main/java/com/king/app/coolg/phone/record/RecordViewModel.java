package com.king.app.coolg.phone.record;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.http.bean.response.GdbRespBean;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
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

    public MutableLiveData<String> studioObserver = new MutableLiveData<>();

    public MutableLiveData<String> videoUrlObserver = new MutableLiveData<>();

    private RecordRepository repository;
    private OrderRepository orderRepository;
    protected PlayRepository playRepository;

    protected Record mRecord;

    private String mSingleImagePath;

    private String mVideoCover;

    protected String mPlayUrl;

    private PlayDuration mPlayDuration;
    private String mUrlToSetCover;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        orderRepository = new OrderRepository();
        playRepository = new PlayRepository();
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

                        checkPlayable();
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

    protected void checkPlayable() {
        Observable<String> observable;
        boolean isTest = false;
        if (isTest) {
            observable = Observable.create(e -> {
                e.onNext("http://192.168.11.206:8080/videos/GOTS07E05.mkv");
            });
        }
        else {
            observable = AppHttpClient.getInstance().getAppService().isServerOnline()
                    .flatMap(bean -> isOnline(bean))
                    .flatMap(result -> {
                        PathRequest request = new PathRequest();
                        request.setPath(mRecord.getDirectory());
                        request.setName(mRecord.getName());
                        return AppHttpClient.getInstance().getAppService().getVideoPath(request);
                    })
                    .flatMap(response -> UrlUtil.toVideoUrl(response));
        }
        observable
                .flatMap(url -> {
                    DebugLog.e(url);
                    mPlayUrl = url;
                    return playRepository.getDuration(mRecord.getId());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PlayDuration>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(PlayDuration duration) {
                        mPlayDuration = duration;
                        DebugLog.e("will play url: " + mPlayUrl);
                        videoUrlObserver.setValue(mPlayUrl);
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

    public int getVideoStartSeek() {
        if (mPlayDuration != null) {
            return mPlayDuration.getDuration();
        }
        return 0;
    }

    private ObservableSource<Boolean> isOnline(GdbRespBean bean) {
        return observer -> {
            if (bean.isOnline()) {
                observer.onNext(true);
            }
            else {
                observer.onError(new Exception("Server offline"));
            }
        };
    }

    public String getVideoCover() {
        return mVideoCover;
    }

    private Observable<Boolean> insertToVideoOrders(ArrayList<CharSequence> orderIds) {
        return Observable.create(e -> {
            if (!ListUtil.isEmpty(orderIds)) {
                for (CharSequence id:orderIds) {
                    long orderId = Long.parseLong(id.toString());
                    if (playRepository.isExist(orderId, mRecord.getId())) {
                        continue;
                    }
                    PlayItem item = new PlayItem();
                    item.setOrderId(orderId);
                    item.setRecordId(mRecord.getId());
                    item.setUrl(mPlayUrl);
                    getDaoSession().getPlayItemDao().insertOrReplace(item);
                }
                getDaoSession().getPlayItemDao().detachAll();
            }
            e.onNext(true);
        });
    }
    /**
     * add to temp play order
     * @param list
     */
    public void addToPlay(ArrayList<CharSequence> list) {
        insertToVideoOrders(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean item) {
                        messageObserver.setValue("Add successfully");
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

    protected void loadImages(Record record) {
        if (ImageProvider.hasRecordFolder(record.getName())) {
            List<String> list = ImageProvider.getRecordPathList(record.getName());
            if (list.size() > 1) {
                mVideoCover = list.get(Math.abs(new Random().nextInt()) % list.size());
                imagesObserver.postValue(list);
            }
            else if (list.size() == 1) {
                mSingleImagePath = list.get(0);
                mVideoCover = mSingleImagePath;
                singleImageObserver.postValue(list.get(0));
            }
        }
        else {
            String path = ImageProvider.getRecordRandomPath(record.getName(), null);
            mSingleImagePath = path;
            mVideoCover = mSingleImagePath;
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
                .flatMap(list -> findStudio(list))
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

    protected ObservableSource<List<FavorRecordOrder>> findStudio(List<FavorRecordOrder> list) {
        return observer -> {
            String studioName = "";
            for (FavorRecordOrder order:list) {
                if (order.getParent() != null) {
                    if (order.getParent().getName().equals(AppConstants.ORDER_STUDIO_NAME)) {
                        studioName = order.getName();
                        break;
                    }
                }
            }
            studioObserver.postValue(studioName);
            observer.onNext(list);
        };
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
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            loadImages(mRecord);
        }
    }

    public void deleteOrderOfRecord(long orderId) {
        getDaoSession().getFavorRecordDao().queryBuilder()
                .where(FavorRecordDao.Properties.OrderId.eq(orderId))
                .where(FavorRecordDao.Properties.RecordId.eq(mRecord.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorRecordDao().detachAll();
    }

    public void resetPlayInDb() {
        playRepository.deleteDuration(mRecord.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

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

    public void updatePlayPosition(int currentPosition) {
        mPlayDuration.setDuration(currentPosition);
    }

    public void updatePlayToDb() {
        if (mPlayDuration != null) {
            DebugLog.e("duration=" + mPlayDuration.getDuration());
            getDaoSession().getPlayDurationDao().insertOrReplace(mPlayDuration);
            getDaoSession().getPlayDurationDao().detachAll();
        }
    }

    public void setUrlToSetCover(String path) {
        mUrlToSetCover = path;
    }

    public void setPlayOrderCover(ArrayList<CharSequence> list) {
        savePlayOrderCover(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        messageObserver.setValue("Set successfully");
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

    private Observable<Boolean> savePlayOrderCover(ArrayList<CharSequence> list) {
        return Observable.create(e -> {
            if (!ListUtil.isEmpty(list)) {
                for (CharSequence sequence:list) {
                    long orderId = Long.parseLong(sequence.toString());
                    PlayOrder order = getDaoSession().getPlayOrderDao().load(orderId);
                    order.setCoverUrl(mUrlToSetCover);
                    getDaoSession().getPlayOrderDao().update(order);
                }
            }
            e.onNext(true);
        });
    }
}
