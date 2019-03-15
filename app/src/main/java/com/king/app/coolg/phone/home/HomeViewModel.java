package com.king.app.coolg.phone.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.ObservableSource;
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

    public MutableLiveData<Record> recommendObserver = new MutableLiveData<>();

    private List<Record> mRecommendList;
    /**
     * 一共缓存3个推荐，previous, next and current
     */
    private Record previousRecord, currentRecord, nextRecord;
    /**
     * 过滤器
     */
    private RecommendBean mRecommendBean;

    private PlayRepository playRepository;

    private Record mRecordAddViewOrder;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        recordRepository = new RecordRepository();
        playRepository = new PlayRepository();
        mHomeBean = new HomeBean();
        mHomeBean.setRecordList(new ArrayList<>());
        mRecommendBean = SettingProperty.getHomeRecBean();
        if (mRecommendBean == null) {
            mRecommendBean = new RecommendBean();
        }

        loadRecommend();
    }

    private void loadRecommend() {
        recordRepository.getRecordsBy(mRecommendBean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        mRecommendList = records;
                        recommendNext();
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

    public void recommendNext() {
        if (nextRecord == null) {
            previousRecord = currentRecord;
            currentRecord = newRecommend();
            nextRecord = null;
            recommendObserver.setValue(currentRecord);
        }
        else {
            recommendObserver.setValue(currentRecord);
            previousRecord = currentRecord;
            currentRecord = nextRecord;
            nextRecord = null;
        }
    }

    public void recommendPrevious() {
        if (previousRecord == null) {
            nextRecord = currentRecord;
            currentRecord = newRecommend();
            previousRecord = null;
            recommendObserver.setValue(currentRecord);
        }
        else {
            recommendObserver.setValue(currentRecord);
            nextRecord = currentRecord;
            currentRecord = previousRecord;
            previousRecord = null;
        }
    }

    public void updateRecordFilter(RecommendBean bean) {
        this.mRecommendBean = bean;
        SettingProperty.setHomeRecBean(bean);
        loadRecommend();
    }

    /**
     * 获得新记录
     * @return
     */
    public Record newRecommend() {
        if (ListUtil.isEmpty(mRecommendList)) {
            return null;
        }
        // 没有设置过滤器的情况，直接随机位置
        if (ListUtil.getSize(mRecommendList) > 0) {
            Random random = new Random();
            int index = Math.abs(random.nextInt()) % mRecommendList.size();
            return mRecommendList.get(index);
        }
        return null;
    }

    /**
     * load home data except recommend
     */
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

    public void insertToPlayList(ArrayList<CharSequence> list) {
        PathRequest request = new PathRequest();
        request.setPath(mRecordAddViewOrder.getDirectory());
        request.setName(mRecordAddViewOrder.getName());
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().getVideoPath(request)
                .flatMap(response -> UrlUtil.toVideoUrl(response))
                .flatMap(url -> insertToPlayerListDb(list, mRecordAddViewOrder.getId(), url))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean pass) {
                        loadingObserver.setValue(false);
                        messageObserver.setValue("success");
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

    private ObservableSource<Boolean> insertToPlayerListDb(ArrayList<CharSequence> orderIds, long recordId, String url) {
        return observer -> {
            if (!ListUtil.isEmpty(orderIds)) {
                for (CharSequence id:orderIds) {
                    long orderId = Long.parseLong(id.toString());
                    if (playRepository.isExist(orderId, recordId)) {
                        continue;
                    }
                    PlayItem item = new PlayItem();
                    item.setOrderId(orderId);
                    item.setRecordId(recordId);
                    item.setUrl(url);
                    getDaoSession().getPlayItemDao().insertOrReplace(item);
                }
                getDaoSession().getPlayItemDao().detachAll();
            }
            observer.onNext(true);
        };
    }

    public void saveRecordToAddViewOrder(Record record) {
        mRecordAddViewOrder = record;
    }

}
