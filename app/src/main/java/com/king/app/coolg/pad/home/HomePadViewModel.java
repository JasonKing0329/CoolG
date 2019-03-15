package com.king.app.coolg.pad.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.FilterHelper;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.RecommendModel;
import com.king.app.coolg.model.bean.RecordFilterModel;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.home.RecommendProvider;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.utils.DebugLog;
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

    /**
     * 过滤器
     */
    private RecommendBean mRecommendBean;
    private RecommendProvider recommendProvider;

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
        mRecordList = new ArrayList<>();
        mRecommendBean = SettingProperty.getHomeRecBean();
        if (mRecommendBean == null) {
            mRecommendBean = new RecommendBean();
        }
        recommendProvider = new RecommendProvider().setCacheTotal(12).setNewCacheWhenNumber(3);
        createRecommend();
    }

    private void createRecommend() {
        DebugLog.e("");
        recommendProvider.create(mRecommendBean, new RecommendProvider.ProviderObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onCreateComplete() {
                DebugLog.e("");
                getRecommends();
                resetTimer();
            }
        });
    }

    private void getRecommends() {
        recommendProvider.getRecommends(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        recommendsObserver.setValue(records);
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

    public void updateRecordFilter(RecommendBean bean) {
        this.mRecommendBean = bean;
        SettingProperty.setHomeRecBean(bean);
        createRecommend();
    }

    public void loadHomeData() {
        queryStars()
                .flatMap(stars -> {
                    starsObserver.postValue(stars);
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
                .subscribe(aLong -> {
                    getRecommends();
                });
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
        resetTimer();
    }

    public void refreshRecAndStars() {
        getRecommends();

        queryStars()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarProxy> list) {
                        starsObserver.postValue(list);
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

    public Record getRecommendedRecord(int index) {
        return recommendsObserver.getValue().get(index);
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
