package com.king.app.coolg.phone.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagStar;
import com.king.app.gdb.data.entity.TagStarDao;

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
    public MutableLiveData<FavorStar> addOrderObserver = new MutableLiveData<>();
    public MutableLiveData<List<Tag>> tagsObserver = new MutableLiveData<>();

    protected Star mStar;
    protected List<String> starImageList;
    private List<StarRelationship> relationList;
    private List<StarStudioTag> studioList;
    protected List<Tag> tagList;

    protected StarRepository starRepository;
    private OrderRepository orderRepository;
    protected RecordRepository recordRepository;

    private RecommendBean mRecordFilter;
    private String mScene;
    private long mStudioId;

    private String mSingleImagePath;

    public StarViewModel(@NonNull Application application) {
        super(application);
        starRepository = new StarRepository();
        orderRepository = new OrderRepository();
        recordRepository = new RecordRepository();
        mScene = AppConstants.KEY_SCENE_ALL;
    }

    public void setRecordFilter(RecommendBean mRecordFilter) {
        this.mRecordFilter = mRecordFilter;
    }

    public RecommendBean getRecordFilter() {
        return mRecordFilter;
    }

    public void setStudioId(long mStudioId) {
        this.mStudioId = mStudioId;
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
                    return orderRepository.getStudioTagByStar(mStar.getId());
                })
                .flatMap(list -> {
                    studioList = list;
                    return getStarTags(mStar);
                })
                .flatMap(list -> {
                    tagList = list;
                    return getComplexFilter();
                })
                .flatMap(filter -> recordRepository.getRecords(filter))
                .flatMap(list -> toProxyRecords(list))
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

    public List<StarStudioTag> getStudioList() {
        return studioList;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    protected Observable<List<String>> getStarImages(Star star) {
        return Observable.create(e -> {
            e.onNext(loadStarImages(star));
            e.onComplete();
        });
    }

    protected ObservableSource<List<Tag>> getStarTags(Star star) {
        return observer -> {
            observer.onNext(getTags(star));
            observer.onComplete();
        };
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
            observer.onComplete();
        };
    }

    private ObservableSource<List<RecordProxy>> toProxyRecords(List<Record> records) {
        return observer -> {
            List<RecordProxy> list = new ArrayList<>();
            for (Record record : records) {
                RecordProxy proxy = new RecordProxy();
                proxy.setRecord(record);
                proxy.setImagePath(ImageProvider.getRecordRandomPath(record.getName(), null));
                list.add(proxy);
            }
            observer.onNext(list);
            observer.onComplete();
        };
    }

    public void loadStarRecords() {
        getComplexFilter()
                .flatMap(filter -> recordRepository.getRecords(filter))
                .flatMap(list -> toProxyRecords(list))
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
            filter.setDesc(SettingProperty.isStarRecordsSortDesc());
            filter.setSortType(SettingProperty.getStarRecordsSortType());
            filter.setStudioId(mStudioId);
            filter.setStarId(mStar.getId());
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
                        addOrderObserver.setValue(favorStar);
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

    public void deleteOrderOfStar(long orderId, long starId) {
        getDaoSession().getFavorStarDao().queryBuilder()
                .where(FavorStarDao.Properties.OrderId.eq(orderId))
                .where(FavorStarDao.Properties.StarId.eq(starId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorStarDao().detachAll();
    }

    public void addTag(Tag tag) {
        TagStar ts = null;
        try {
            ts = getDaoSession().getTagStarDao().queryBuilder()
                    .where(TagStarDao.Properties.StarId.eq(mStar.getId()))
                    .where(TagStarDao.Properties.TagId.eq(tag.getId()))
                    .build().unique();
        } catch (Exception e) {}
        if (ts == null) {
            ts = new TagStar();
            ts.setStarId(mStar.getId());
            ts.setTagId(tag.getId());
            getDaoSession().getTagStarDao().insert(ts);
            getDaoSession().getTagStarDao().detachAll();
            refreshTags();
        }
    }

    public void refreshTags() {
        tagsObserver.postValue(getTags(mStar));
    }

    private List<Tag> getTags(Star star) {
        List<TagStar> list = getDaoSession().getTagStarDao().queryBuilder()
                .where(TagStarDao.Properties.StarId.eq(star.getId()))
                .build().list();
        List<Tag> result = new ArrayList<>();
        for (TagStar tr:list) {
            if (tr.getTag() != null) {
                result.add(tr.getTag());
            }
        }
        return result;
    }

    public void deleteTag(Tag bean) {
        getDaoSession().getTagStarDao().queryBuilder()
                .where(TagStarDao.Properties.StarId.eq(mStar.getId()))
                .where(TagStarDao.Properties.TagId.eq(bean.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        getDaoSession().getTagRecordDao().detachAll();
        refreshTags();
    }
    private class RelationComparator implements Comparator<StarRelationship> {

        @Override
        public int compare(StarRelationship left, StarRelationship right) {
            return right.getCount() - left.getCount();
        }
    }

}
