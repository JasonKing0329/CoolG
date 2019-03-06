package com.king.app.coolg.phone.studio.page;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @date: 2018/9/18 11:15
 */
public class StudioPageViewModel extends BaseViewModel {

    public MutableLiveData<StudioPageItem> pageObserver = new MutableLiveData<>();

    public StudioPageViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadPageData(long studioId) {
        loadingObserver.setValue(true);
        getStudio(studioId)
                .flatMap(order -> toPageItem(order))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<StudioPageItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(StudioPageItem studioPageItem) {
                        loadingObserver.setValue(false);
                        pageObserver.setValue(studioPageItem);
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

    private Observable<FavorRecordOrder> getStudio(long studioId) {
        return Observable.create(e -> e.onNext(getDaoSession().getFavorRecordOrderDao().load(studioId)));
    }

    private ObservableSource<StudioPageItem> toPageItem(FavorRecordOrder order) {
        return observer -> {
            StudioPageItem pageItem = new StudioPageItem();
            pageItem.setOrder(order);
            pageItem.setRecordList(new ArrayList<>());
            pageItem.setStarList(new ArrayList<>());
            Map<Long, StarNumberItem> starMap = new HashMap<>();
            int starCount = 0;
            int highCount = 0;
            for (int i = 0; i < order.getRecordList().size(); i ++) {
                Record record = order.getRecordList().get(i);
                RecordProxy proxy = new RecordProxy();
                proxy.setRecord(record);
                proxy.setImagePath(ImageProvider.getRecordRandomPath(record.getName(), null));
                proxy.setOffsetIndex(i);
                pageItem.getRecordList().add(proxy);

                List<RecordStar> stars = record.getRelationList();
                for (RecordStar star:stars) {
                    StarNumberItem item = starMap.get(star.getStarId());
                    if (item == null) {
                        item = new StarNumberItem();
                        item.setStar(star.getStar());
                        item.setName(star.getStar().getName());
                        item.setImageUrl(ImageProvider.getStarRandomPath(item.getName(), null));
                        pageItem.getStarList().add(item);
                        starMap.put(star.getStarId(), item);
                        starCount ++;
                    }
                    item.setNumber(item.getNumber() + 1);
                }
                if (record.getScore() >= 400) {
                    highCount ++;
                }
            }

            pageItem.setStrCount(pageItem.getRecordList().size() + " Videos, " + starCount + " Stars");
            if (highCount > 0) {
                pageItem.setStrHighCount(highCount + " 400+ Videos");
            }
            pageItem.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd").format(order.getUpdateTime()));

            // sort by star's records number, desc
            Collections.sort(pageItem.getStarList(), new StarNumberComparator());
            // pick the top 9 stars
            if (pageItem.getStarList().size() > 9) {
                pageItem.setStarList(pageItem.getStarList().subList(0, 9));
            }
            // sort records by score, desc
            Collections.sort(pageItem.getRecordList(), new RecordComparator());
            observer.onNext(pageItem);
        };
    }

    private class StarNumberComparator implements Comparator<StarNumberItem> {
        @Override
        public int compare(StarNumberItem o1, StarNumberItem o2) {
            return o2.getNumber() - o1.getNumber();
        }
    }

    private class RecordComparator implements Comparator<RecordProxy> {
        @Override
        public int compare(RecordProxy o1, RecordProxy o2) {
            return o2.getRecord().getScore() - o1.getRecord().getScore();
        }
    }

}
