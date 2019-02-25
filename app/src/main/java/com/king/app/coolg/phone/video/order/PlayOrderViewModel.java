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
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlayOrderViewModel extends BaseViewModel {

    public MutableLiveData<List<VideoPlayList>> dataObserver = new MutableLiveData<>();

    public PlayOrderViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadOrders() {
        getOrders()
                .flatMap(list -> toViewItems(list))
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
}
