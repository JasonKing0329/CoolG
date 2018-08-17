package com.king.app.coolg.pad.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.FilterHelper;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.RecommendModel;
import com.king.app.coolg.model.bean.RecordFilterModel;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
 * @date: 2018/8/17 10:31
 */
public class HomePadViewModel extends BaseViewModel {

    private final int LOAD_NUM = 20;

    private int mOffset;

    private StarRepository starRepository;
    private RecordRepository recordRepository;

    private RecommendModel recommendModel;
    private List<Record> recommendList;
    private List<Record> recommendedList;
    private RecordFilterModel mRecordFilter;

    private List<Object> mRecordList;
    private String mLastDay;

    public MutableLiveData<List<StarProxy>> starsObserver = new MutableLiveData<>();
    public MutableLiveData<List<Record>> recommendsObserver = new MutableLiveData<>();
    public MutableLiveData<List<Object>> recordsObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> moreObserver = new MutableLiveData<>();
    private Disposable timerDisposable;

    public HomePadViewModel(@NonNull Application application) {
        super(application);
        starRepository = new StarRepository();
        recordRepository = new RecordRepository();
        recommendModel = new RecommendModel();
        mRecordFilter = new FilterHelper().getFilters();
        mRecordList = new ArrayList<>();
    }

    public void updateRecordFilter(RecordFilterModel mRecordFilter) {
        this.mRecordFilter = mRecordFilter;
    }

    public void loadHomeData() {
        queryStars()
                .flatMap(stars -> {
                    starsObserver.postValue(stars);
                    return queryRecommend();
                })
                .flatMap(list -> {
                    createTimer();
                    recommendsObserver.postValue(list);
                    return queryRecords();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> list) {
                        recordsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load data error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<StarProxy>> queryStars() {
        return Observable.create(e -> {
            List<StarProxy> starList = new ArrayList<>();
            // 随机获取N个favor
            List<Star> favorList = starRepository.getRandomRatingAbove(StarRatingUtil.RATING_VALUE_CP, 10);
            for (int i = 0; i < favorList.size(); i ++) {
                StarProxy proxy = new StarProxy();
                Star star = favorList.get(i);
                proxy.setStar(star);
                proxy.setImagePath(ImageProvider.getStarRandomPath(star.getName(), null));
                starList.add(proxy);
            }
            // 不够则从star中随机抽取剩余所需
            if (favorList.size() < 10) {
                int size = 10 - favorList.size();
                favorList.addAll(starRepository.getRandomStars(size));
            }

            for (Star star:favorList) {
                StarProxy proxy = new StarProxy();
                proxy.setStar(star);
                proxy.setImagePath(ImageProvider.getStarRandomPath(star.getName(), null));
                starList.add(proxy);
            }
            e.onNext(starList);
        });
    }

    private Observable<List<Record>> queryRecommend() {
        return Observable.create(e -> {
            recommendList = getDaoSession().getRecordDao()
                    .queryBuilder()
                    .orderDesc(RecordDao.Properties.LastModifyTime)
                    .build().list();
            recommendedList = new ArrayList<>();
            recommendedList.add(newRecord());
            recommendedList.add(newRecord());
            recommendedList.add(newRecord());
            e.onNext(recommendedList);
        });
    }

    private Observable<List<Object>> queryRecords() {
        return recordRepository.getLatestRecords(mOffset, LOAD_NUM)
                .flatMap(list -> toViewRecords(list));
    }

    private ObservableSource<List<Object>> toViewRecords(List<Record> list) {
        return observer -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Record record:list) {
                String day = sdf.format(new Date(record.getLastModifyTime()));
                if (!day.equals(mLastDay)) {
                    mRecordList.add(day);
                    mLastDay = day;
                }
                mRecordList.add(record);
            }
            mOffset += list.size();
            observer.onNext(mRecordList);
        };
    }

    private void createTimer() {
        int time = SettingProperty.getRecommendAnimTime();
        if (time < 3000) {
            time = 8000;
        }
        timerDisposable = Observable.interval(time, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> refreshRec());
    }

    public void resetTimer() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
        createTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
    }

    public void onStop() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
    }

    public void onResume() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
        createTimer();
    }

    public void refreshRec() {
        queryRecommend()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        recommendsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load data error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void refreshRecAndStars() {
        queryStars()
                .flatMap(stars -> {
                    starsObserver.postValue(stars);
                    return queryRecommend();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        recommendsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load data error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获得新记录
     * @return
     */
    public Record newRecord() {
        if (recommendList == null || recommendList.size() == 0) {
            return null;
        }
        // 没有设置过滤器的情况，直接随机位置
        if (mRecordFilter == null) {
            Random random = new Random();
            int index = Math.abs(random.nextInt()) % recommendList.size();
            return recommendList.get(index);
        }
        else {// 打乱当前所有记录，选出第一个符合过滤器条件的记录
            Collections.shuffle(recommendList);
            boolean pass;
            for (Record record: recommendList) {
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

    public Record getRecommendedRecord(int index) {
        return recommendedList.get(index);
    }

    public void loadMore() {
        final int startOffset = mOffset;
        queryRecords()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> objects) {
                        if (mOffset != startOffset) {
                            moreObserver.setValue(startOffset + 1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("loadMore error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
