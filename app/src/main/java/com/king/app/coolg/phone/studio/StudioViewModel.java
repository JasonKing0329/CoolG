package com.king.app.coolg.phone.studio;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.studio.page.StarNumberItem;
import com.king.app.coolg.phone.studio.page.StudioPageItem;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
public class StudioViewModel extends BaseViewModel {

    public MutableLiveData<List<StudioSimpleItem>> simpleObserver = new MutableLiveData<>();
    public MutableLiveData<List<StudioRichItem>> richObserver = new MutableLiveData<>();

    public MutableLiveData<StudioPageItem> pageObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> listLoadingObserver = new MutableLiveData<>();
    public MutableLiveData<String> listMessageObserver = new MutableLiveData<>();
    public MutableLiveData<Boolean> pageLoadingObserver = new MutableLiveData<>();
    public MutableLiveData<String> pageMessageObserver = new MutableLiveData<>();

    public MutableLiveData<String> listTypeMenuObserver = new MutableLiveData<>();

    public StudioViewModel(@NonNull Application application) {
        super(application);
    }

    public void toggleListType() {
        if (PreferenceValue.STUDIO_LIST_TYPE_RICH == SettingProperty.getStudioListType()) {
            SettingProperty.setStudioListType(PreferenceValue.STUDIO_LIST_TYPE_SIMPLE);
        }
        else {
            SettingProperty.setStudioListType(PreferenceValue.STUDIO_LIST_TYPE_RICH);
        }
        loadStudios();
    }

    public void onSortTypeChanged(int sortType) {
        int curType = SettingProperty.getStudioListSortType();
        if (sortType != curType) {
            SettingProperty.setStudioListSortType(sortType);
            loadStudios();
        }
    }

    public void loadStudios() {
        if (PreferenceValue.STUDIO_LIST_TYPE_RICH == SettingProperty.getStudioListType()) {
            listTypeMenuObserver.setValue("Simple List");
            loadRichItems();
        }
        else {
            listTypeMenuObserver.setValue("Rich List");
            loadSimpleItems();
        }
    }

    private void loadSimpleItems() {
        listLoadingObserver.setValue(true);
        getStudios()
                .flatMap(list -> toSimpleItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StudioSimpleItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StudioSimpleItem> studioSimpleItems) {
                        listLoadingObserver.setValue(false);
                        simpleObserver.setValue(studioSimpleItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listLoadingObserver.setValue(false);
                        listMessageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRichItems() {
        listLoadingObserver.setValue(true);
        getStudios()
                .flatMap(list -> toRichItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StudioRichItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StudioRichItem> richItems) {
                        listLoadingObserver.setValue(false);
                        richObserver.setValue(richItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listLoadingObserver.setValue(false);
                        listMessageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<FavorRecordOrder>> getStudios() {
        return Observable.create(e -> {
            FavorRecordOrder studio = getDaoSession().getFavorRecordOrderDao().queryBuilder()
                    .where(FavorRecordOrderDao.Properties.Name.eq("Studio"))
                    .build().uniqueOrThrow();
            int sortType = SettingProperty.getStudioListSortType();
            QueryBuilder<FavorRecordOrder> builder = getDaoSession().getFavorRecordOrderDao().queryBuilder();
            builder.where(FavorRecordOrderDao.Properties.ParentId.eq(studio.getId()));
            if (sortType == PreferenceValue.STUDIO_LIST_SORT_NUM) {
                builder.orderDesc(FavorRecordOrderDao.Properties.Number);
            }
            else if (sortType == PreferenceValue.STUDIO_LIST_SORT_CREATE_TIME) {
                builder.orderDesc(FavorRecordOrderDao.Properties.CreateTime);
            }
            else if (sortType == PreferenceValue.STUDIO_LIST_SORT_UPDATE_TIME) {
                builder.orderDesc(FavorRecordOrderDao.Properties.UpdateTime);
            }
            else {
                builder.orderAsc(FavorRecordOrderDao.Properties.Name);
            }
            List<FavorRecordOrder> list = builder.build().list();
            e.onNext(list);
        });
    }

    private ObservableSource<List<StudioSimpleItem>> toSimpleItems(List<FavorRecordOrder> list) {
        return observer -> {
            List<StudioSimpleItem> result = new ArrayList<>();
            for (FavorRecordOrder order:list) {
                StudioSimpleItem item = new StudioSimpleItem();
                item.setOrder(order);
                item.setFirstChar(String.valueOf(order.getName().charAt(0)));
                item.setName(order.getName());
                item.setNumber(String.valueOf(order.getRecordList().size()));
                result.add(item);
            }
            observer.onNext(result);
        };
    }

    private ObservableSource<List<StudioRichItem>> toRichItems(List<FavorRecordOrder> list) {
        return observer -> {
            List<StudioRichItem> result = new ArrayList<>();
            for (FavorRecordOrder order:list) {
                StudioRichItem item = new StudioRichItem();
                item.setOrder(order);
                if (!SettingProperty.isNoImageMode()) {
                    item.setImageUrl(order.getCoverUrl());
                }
                item.setName(order.getName());
                countRichInfo(order, item);
                result.add(item);
            }
            observer.onNext(result);
        };
    }

    private void countRichInfo(FavorRecordOrder order, StudioRichItem item) {
        int videos = order.getRecordList().size();
        int countHigh = 0;
        TreeSet<Long> starIdList = new TreeSet<>();
        for (Record record:order.getRecordList()) {
            if (record.getScore() >= 400) {
                countHigh ++;
            }
            List<RecordStar> stars = record.getRelationList();
            for (RecordStar star:stars) {
                starIdList.add(star.getId());
            }
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(videos).append(" Videos");
        if (starIdList.size() > 0) {
            buffer.append(", ").append(starIdList.size()).append(" Stars");
        }
        item.setCount(buffer.toString());

        if (countHigh > 0) {
            item.setHigh(countHigh + " 400+ Videos");
        }
    }

    public void loadPageData(long studioId) {
        pageLoadingObserver.setValue(true);
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
                        pageLoadingObserver.setValue(false);
                        pageObserver.setValue(studioPageItem);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        pageLoadingObserver.setValue(false);
                        pageMessageObserver.setValue(e.getMessage());
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
