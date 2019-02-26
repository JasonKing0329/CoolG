package com.king.app.coolg.phone.video.order;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.phone.video.home.VideoPlayList;
import com.king.app.gdb.data.entity.PlayItemDao;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.VideoCoverPlayOrderDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlayOrderViewModel extends BaseViewModel {

    private final int SORT_BY_ID = 0;

    private final int SORT_BY_NAME = 1;

    private int mSortType = SORT_BY_ID;

    public MutableLiveData<List<VideoPlayList>> dataObserver = new MutableLiveData<>();

    public PlayOrderViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadOrders() {
        getOrders()
                .flatMap(list -> toViewItems(list))
                .flatMap(list -> sort(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<VideoPlayList>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<VideoPlayList> videoPlayLists) {
                        dataObserver.setValue(videoPlayLists);
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

    private Observable<List<PlayOrder>> getOrders() {
        return Observable.create(e -> e.onNext(getDaoSession().getPlayOrderDao().loadAll()));
    }

    private ObservableSource<List<VideoPlayList>> toViewItems(List<PlayOrder> list) {
        return observer -> {
            List<VideoPlayList> result = new ArrayList<>();
            for (PlayOrder order:list) {
                VideoPlayList playList = new VideoPlayList();
                playList.setName(order.getName());
                playList.setImageUrl(order.getCoverUrl());
                playList.setPlayOrder(order);
                playList.setVisibility(View.GONE);

                long count = getDaoSession().getPlayItemDao().queryBuilder()
                        .where(PlayItemDao.Properties.OrderId.eq(order.getId()))
                        .buildCount().count();
                playList.setVideos((int) count);

                result.add(playList);
            }
            observer.onNext(result);
        };
    }

    public void addPlayOrder(String name) {
        PlayOrder order = new PlayOrder();
        order.setName(name);
        getDaoSession().getPlayOrderDao().insert(order);
        getDaoSession().getPlayOrderDao().detachAll();

        loadOrders();
    }

    public void executeDelete() {
        if (dataObserver.getValue() != null) {
            for (VideoPlayList item:dataObserver.getValue()) {
                if (item.isChecked()) {
                    // delete from play_order
                    getDaoSession().getPlayOrderDao().deleteByKey(item.getPlayOrder().getId());
                    // delete from play_item
                    getDaoSession().getPlayItemDao().queryBuilder()
                            .where(PlayItemDao.Properties.OrderId.eq(item.getPlayOrder().getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                    // delete from video_cover_play_Order
                    getDaoSession().getVideoCoverPlayOrderDao().queryBuilder()
                            .where(VideoCoverPlayOrderDao.Properties.OrderId.eq(item.getPlayOrder().getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
            }
            getDaoSession().getPlayOrderDao().detachAll();
            getDaoSession().getPlayItemDao().detachAll();
            loadOrders();
        }
    }

    public ArrayList<CharSequence> getSelectedItems() {
        ArrayList<CharSequence> list = new ArrayList<>();
        if (dataObserver.getValue() != null) {
            for (VideoPlayList item : dataObserver.getValue()) {
                if (item.isChecked()) {
                    list.add(String.valueOf(item.getPlayOrder().getId()));
                }
            }
        }
        return list;
    }

    public void updateOrderName(VideoPlayList data, String name) {
        data.setName(name);
        data.getPlayOrder().setName(name);
        getDaoSession().getPlayOrderDao().update(data.getPlayOrder());
    }

    private Observable<List<VideoPlayList>> sort(List<VideoPlayList> list) {
        return Observable.create(e -> {
            if (mSortType == SORT_BY_NAME) {
                Collections.sort(list, new NameComparator());
            }
            else {
                Collections.sort(list, new IdComparator());
            }
            e.onNext(list);
        });
    }

    public void sortById() {
        if (mSortType != SORT_BY_ID) {
            mSortType = SORT_BY_ID;
            sort();
        }
    }

    public void sortByName() {
        if (mSortType != SORT_BY_NAME) {
            mSortType = SORT_BY_NAME;
            sort();
        }
    }

    private void sort() {
        loadingObserver.setValue(true);
        sort(dataObserver.getValue())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<VideoPlayList>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<VideoPlayList> list) {
                        dataObserver.setValue(list);
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private class IdComparator implements Comparator<VideoPlayList> {

        @Override
        public int compare(VideoPlayList left, VideoPlayList right) {
            long result = left.getPlayOrder().getId() - right.getPlayOrder().getId();
            if (result < 0) {
                return -1;
            }
            else if (result > 0) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    private class NameComparator implements Comparator<VideoPlayList> {

        @Override
        public int compare(VideoPlayList left, VideoPlayList right) {
            return left.getName().toLowerCase().compareTo(right.getName().toLowerCase());
        }
    }
}
