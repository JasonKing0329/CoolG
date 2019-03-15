package com.king.app.coolg.phone.home;

import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/15 10:37
 */
public class RecommendProvider {

    private LinkedList<Record> cacheQueue;
    private RecordRepository repository;

    private int cacheTotal;
    private int newCacheWhenNumber;

    private RecommendBean mBean;

    private CompositeDisposable composite;

    public RecommendProvider() {
        cacheQueue = new LinkedList<>();
        repository = new RecordRepository();
    }

    public RecommendProvider setCacheTotal(int cacheTotal) {
        this.cacheTotal = cacheTotal;
        return this;
    }

    public RecommendProvider setNewCacheWhenNumber(int newCacheWhenNumber) {
        this.newCacheWhenNumber = newCacheWhenNumber;
        return this;
    }

    public void create(RecommendBean bean, ProviderObserver observer) {
        mBean = bean;
        loadCache(cacheTotal, true, observer);
    }

    private void loadCache(int number, boolean clearCache, ProviderObserver observer) {
        mBean.setNumber(number);
        repository.getRecordsBy(mBean)
                .flatMap(list -> Observable.fromIterable(list))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Record>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (observer != null) {
                            observer.onSubscribe(d);
                        }
                        if (clearCache) {
                            clearCache();
                        }
                    }

                    @Override
                    public void onNext(Record record) {
                        offerCache(record);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if (observer != null) {
                            observer.onCreateComplete();
                        }
                    }
                });
    }

    public Observable<Record> getRecommend() {
        return Observable.create(e -> e.onNext(provideRecommend()));
    }

    public Observable<List<Record>> getRecommends(int number) {
        return Observable.create(e -> e.onNext(provideRecommends(number)));
    }

    private synchronized void clearCache() {
        cacheQueue.clear();
    }

    private synchronized void offerCache(Record record) {
        cacheQueue.offer(record);
    }

    private synchronized Record provideRecommend() {
        Record record = cacheQueue.poll();
        checkIfCache();
        return record;
    }

    private synchronized List<Record> provideRecommends(int number) {
        List<Record> list = new ArrayList<>();
        for (int i = 0; i < number; i ++) {
            Record record = cacheQueue.poll();
            if (record != null) {
                list.add(record);
            }
        }
        checkIfCache();
        return list;
    }

    private synchronized void checkIfCache() {
        if (cacheQueue.size() <= newCacheWhenNumber) {
            loadCache(cacheTotal - cacheQueue.size(), false, null);
        }
    }

    public interface ProviderObserver {
        void onSubscribe(Disposable d);
        void onCreateComplete();
    }
}
