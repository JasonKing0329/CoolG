package com.king.app.coolg.phone.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.io.File;
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
 * Created by Administrator on 2018/8/12 0012.
 */

public class StarViewModel extends BaseViewModel {

    public MutableLiveData<Star> starObserver = new MutableLiveData<>();
    public MutableLiveData<List<RecordProxy>> recordsObserver = new MutableLiveData<>();
    public MutableLiveData<List<RecordProxy>> onlyRecordsObserver = new MutableLiveData<>();
    public MutableLiveData<List<FavorStarOrder>> ordersObserver = new MutableLiveData<>();

    protected Star mStar;
    private List<String> starImageList;
    private List<StarRelationship> relationList;

    protected StarRepository starRepository;
    private OrderRepository orderRepository;

    private RecordListFilterBean mRecordFilter;
    private String mScene;

    private String mSingleImagePath;

    public StarViewModel(@NonNull Application application) {
        super(application);
        starRepository = new StarRepository();
        orderRepository = new OrderRepository();
        mScene = AppConstants.KEY_SCENE_ALL;
    }

    public void setRecordFilter(RecordListFilterBean mRecordFilter) {
        this.mRecordFilter = mRecordFilter;
    }

    public RecordListFilterBean getRecordFilter() {
        return mRecordFilter;
    }

    public void loadStar(long starId) {
        loadingObserver.setValue(true);
        starRepository.getStar(starId)
                .flatMap(star -> {
                    mStar = star;
                    starObserver.postValue(mStar);
                    return getStarImages(star);
                })
                .flatMap(list -> {
                    starImageList = list;
                    return getRelationships(mStar);
                })
                .flatMap(list -> {
                    relationList = list;
                    return getComplexFilter();
                })
                .flatMap(filter -> getStarRecords(mStar, filter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> recordProxies) {
                        loadingObserver.setValue(false);
                        recordsObserver.setValue(recordProxies);
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

    public Star getStar() {
        return mStar;
    }

    public List<String> getStarImageList() {
        return starImageList;
    }

    public List<StarRelationship> getRelationList() {
        return relationList;
    }

    protected ObservableSource<List<String>> getStarImages(Star star) {
        return observer -> observer.onNext(loadStarImages(star));
    }

    protected ObservableSource<List<StarRelationship>> getRelationships(Star star) {
        return observer -> {
            List<StarRelationship> list = new ArrayList<>();
            Map<String, StarRelationship> map = new HashMap<>();
            for (Record record:star.getRecordList()) {
                for (Star st:record.getStarList()) {
                    if (st.getId() == mStar.getId()) {
                        continue;
                    }
                    StarRelationship relationship = map.get(st.getName());
                    if (relationship == null) {
                        relationship = new StarRelationship();
                        relationship.setStar(st);
                        relationship.setImagePath(ImageProvider.getStarRandomPath(st.getName(), null));
                        map.put(st.getName(), relationship);
                        list.add(relationship);
                    }
                    relationship.setCount(relationship.getCount() + 1);
                }
            }
            Collections.sort(list, new RelationComparator());
            observer.onNext(list);
        };
    }

    private ObservableSource<List<RecordProxy>> getStarRecords(Star star, RecordComplexFilter filter) {
        return observer -> {
            List<RecordProxy> list = new ArrayList<>();
            for (Record record:star.getRecordList()) {
                if (filterRecord(filter, record)) {
                    RecordProxy proxy = new RecordProxy();
                    proxy.setRecord(record);
                    proxy.setImagePath(ImageProvider.getRecordRandomPath(record.getName(), null));
                    list.add(proxy);
                }
            }
            RecordsComparator comparator = new RecordsComparator(filter.getSortType(), filter.getDesc());
            Collections.sort(list, comparator);
            observer.onNext(list);
        };
    }

    private boolean filterRecord(RecordComplexFilter filter, Record record) {
        boolean result = true;
        if (filter.getRecordType() != 0) {
            if (record.getType() != filter.getRecordType()) {
                return false;
            }
        }
        if (filter.getFilter() != null) {
            if (filter.getFilter().isBareback()) {
                if (record.getScoreBareback() == 0) {
                    return false;
                }
            }
            if (filter.getFilter().isInnerCum()) {
                if (TextUtils.isEmpty(record.getSpecialDesc()) || !record.getSpecialDesc().contains("inner cum")) {
                    return false;
                }
            }
            if (filter.getFilter().isNotDeprecated()) {
                if (record.getDeprecated() == 1) {
                    return false;
                }
            }
        }
        if (!TextUtils.isEmpty(filter.getScene())) {
            if (!filter.getScene().equals(record.getScene())) {
                return false;
            }
        }
        if (!TextUtils.isEmpty(filter.getNameLike())) {
            if (!record.getScene().contains(filter.getNameLike())) {
                return false;
            }
        }
        return result;
    }

    public void loadStarRecords() {
        getComplexFilter()
                .flatMap(filter -> getStarRecords(mStar, filter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> recordProxies) {
                        loadingObserver.setValue(false);
                        onlyRecordsObserver.setValue(recordProxies);
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

    private Observable<RecordComplexFilter> getComplexFilter() {
        return Observable.create(e -> {
            RecordComplexFilter filter = new RecordComplexFilter();
            filter.setFilter(mRecordFilter);
            filter.setRecordType(0);
            filter.setDesc(SettingProperty.isStarRecordsSortDesc());
            filter.setSortType(SettingProperty.getStarRecordsSortType());
            if (!AppConstants.KEY_SCENE_ALL.equals(mScene)) {
                filter.setScene(mScene);
            }
            e.onNext(filter);
        });
    }

    public String getScene() {
        return mScene;
    }

    public void setScene(String mScene) {
        this.mScene = mScene;
    }

    public void loadStarOrders(long starId) {
        orderRepository.getStarOrders(starId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<FavorStarOrder>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<FavorStarOrder> list) {
                        ordersObserver.setValue(list);
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

    public void addToOrder(long orderId) {
        orderRepository.addFavorStar(orderId, mStar.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<FavorStar>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(FavorStar favorStar) {
                        messageObserver.setValue("Add successfully");
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

    private List<String> loadStarImages(Star star) {
        List<String> list = new ArrayList<>();
        if (ImageProvider.hasStarFolder(star.getName())) {
            list = ImageProvider.getStarPathList(star.getName());
            if (list.size() == 1) {
                mSingleImagePath = list.get(0);
            }
        }
        else {
            String path = ImageProvider.getStarRandomPath(star.getName(), null);
            mSingleImagePath = path;
            list.add(path);
        }
        return list;
    }

    public void deleteImage(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        starImageList = loadStarImages(mStar);
    }

    private class RelationComparator implements Comparator<StarRelationship> {

        @Override
        public int compare(StarRelationship left, StarRelationship right) {
            return right.getCount() - left.getCount();
        }
    }

    private class RecordsComparator implements Comparator<RecordProxy> {

        private final int sortValue;
        private final boolean desc;

        public RecordsComparator(int sortType, boolean desc) {
            this.sortValue = sortType;
            this.desc = desc;
        }

        @Override
        public int compare(RecordProxy left, RecordProxy right) {
            if (sortValue == PreferenceValue.GDB_SR_ORDERBY_DATE) {
                return compareLong(left.getRecord().getLastModifyTime(), right.getRecord().getLastModifyTime(), desc);
            }
            else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCORE) {
                return compareInt(left.getRecord().getScore(), right.getRecord().getScore(), desc);
            }
            else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_PASSION) {
                return compareInt(left.getRecord().getScorePassion(), right.getRecord().getScorePassion(), desc);
            }
            else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_CUM) {
                return compareInt(left.getRecord().getScoreCum(), right.getRecord().getScoreCum(), desc);
            }
            else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_STAR) {
                return compareInt(left.getRecord().getScoreStar(), right.getRecord().getScoreStar(), desc);
            }
            else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL) {
                return compareInt(left.getRecord().getScoreFeel(), right.getRecord().getScoreFeel(), desc);
            }
            else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SPECIAL) {
                return compareInt(left.getRecord().getScoreSpecial(), right.getRecord().getScoreSpecial(), desc);
            }
            else {// sort by name
                return compareString(left.getRecord().getName(), right.getRecord().getName(), desc);
            }
        }

        private int compareLong(long left, long right, boolean desc) {
            if (left - right < 0) {
                return desc ? 1:-1;
            }
            else if (left - right > 0) {
                return desc ? -1:1;
            }
            else {
                return 0;
            }
        }

        private int compareInt(int left, int right, boolean desc) {
            if (left - right < 0) {
                return desc ? 1:-1;
            }
            else if (left - right > 0) {
                return desc ? -1:1;
            }
            else {
                return 0;
            }
        }

        private int compareString(String left, String right, boolean desc) {
            if (desc) {
                return right.compareTo(left);
            }
            else {
                return left.compareTo(right);
            }
        }
    }

}
