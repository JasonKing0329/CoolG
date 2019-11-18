package com.king.app.coolg.phone.studio;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
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
        loadingObserver.setValue(true);
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
                        loadingObserver.setValue(false);
                        simpleObserver.setValue(studioSimpleItems);
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

    private void loadRichItems() {
        loadingObserver.setValue(true);
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
                        loadingObserver.setValue(false);
                        richObserver.setValue(richItems);
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

    private Observable<List<FavorRecordOrder>> getStudios() {
        return Observable.create(e -> {
            FavorRecordOrder studio = getDaoSession().getFavorRecordOrderDao().queryBuilder()
                    .where(FavorRecordOrderDao.Properties.Name.eq(AppConstants.ORDER_STUDIO_NAME))
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
                item.setImageUrl(ImageProvider.parseCoverUrl(order.getCoverUrl()));
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
            if (record.getScore() >= 600) {
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
            item.setHigh("600+ Videos: " + countHigh);
        }
    }

}
