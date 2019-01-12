package com.king.app.coolg.phone.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.FilterHelper;
import com.king.app.coolg.model.RecommendModel;
import com.king.app.coolg.model.bean.RecordFilterModel;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
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
    private RecordFilterModel mRecordFilter;
    private RecommendModel recommendModel;

    private PlayRepository playRepository;

    public MutableLiveData<List<PlayItem>> playListObserver = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        recordRepository = new RecordRepository();
        playRepository = new PlayRepository();
        mHomeBean = new HomeBean();
        mHomeBean.setRecordList(new ArrayList<>());

        recommendModel = new RecommendModel();
        mRecordFilter = new FilterHelper().getFilters();
        loadRecommend();
    }

    private void loadRecommend() {
        recordRepository.getAll()
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

    public void updateRecordFilter(RecordFilterModel mRecordFilter) {
        this.mRecordFilter = mRecordFilter;
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
        if (mRecordFilter == null) {
            Random random = new Random();
            int index = Math.abs(random.nextInt()) % mRecommendList.size();
            return mRecommendList.get(index);
        }
        else {// 打乱当前所有记录，选出第一个符合过滤器条件的记录
            Collections.shuffle(mRecommendList);
            boolean pass;
            for (Record record:mRecommendList) {
                pass = true;
                // 记录是NR并且过滤器勾选了支持NR才判定为通过
                if (record.getHdLevel() == DataConstants.RECORD_HD_NR && mRecordFilter.isSupportNR()) {
                    pass = true;
                }
                // 普通记录，以及是NR但是过滤器没有勾选NR，需要检测其他过滤项
                else {
                    boolean result = recommendModel.checkItem(record, mRecordFilter);
                    pass = pass && result;
                }
                if (pass) {
                    return record;
                }
            }
            return null;
        }
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

    public void insertToPlayList(Record record) {
        PathRequest request = new PathRequest();
        request.setPath(record.getDirectory());
        request.setName(record.getName());
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().getVideoPath(request)
                .flatMap(response -> UrlUtil.toVideoUrl(response))
                .flatMap(url -> insertToPlayerListDb(record.getId(), url))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PlayItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(PlayItem playItem) {
                        loadingObserver.setValue(false);
                        messageObserver.setValue("success");
                        loadPlayList();
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

    private ObservableSource<PlayItem> insertToPlayerListDb(long recordId, String url) {
        return playRepository.checkExistence(AppConstants.PLAY_ORDER_TEMP_ID, recordId)
                .flatMap(result -> {
                    PlayItem item = new PlayItem();
                    item.setOrderId(AppConstants.PLAY_ORDER_TEMP_ID);
                    item.setRecordId(recordId);
                    item.setUrl(url);
                    return playRepository.insertOrReplacePlayItem(item);
                });
    }

    public void loadPlayList() {
        playRepository.getPlayItems(AppConstants.PLAY_ORDER_TEMP_ID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlayItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlayItem> playItems) {
                        playListObserver.setValue(playItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load play list error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
