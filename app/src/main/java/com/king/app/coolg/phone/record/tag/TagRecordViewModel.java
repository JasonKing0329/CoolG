package com.king.app.coolg.phone.record.tag;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.phone.record.list.RecordListViewModel;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagDao;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/5 17:11
 */
public class TagRecordViewModel extends RecordListViewModel {

    public MutableLiveData<List<Tag>> tagsObserver = new MutableLiveData<>();
    public MutableLiveData<List<RecordProxy>> recordsObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> moreObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> scrollPositionObserver = new MutableLiveData<>();

    private List<RecordProxy> mRecordList;

    private long mTagId;

    public TagRecordViewModel(@NonNull Application application) {
        super(application);
        onSortTypeChanged();
    }

    public void loadTags() {
        List<Tag> tagList = getDaoSession().getTagDao().queryBuilder().where(TagDao.Properties.Type.eq(DataConstants.TAG_TYPE_RECORD)).build().list();
        Tag all = new Tag();
        all.setId(0l);
        all.setName("All");
        tagList.add(0, all);
        tagsObserver.setValue(tagList);

        loadTagRecords(all.getId());
    }

    public void loadTagRecords() {
        loadTagRecords(mTagId);
    }

    public void loadTagRecords(long tagId) {
        mTagId = tagId;
        // 偏移量从0开始
        newRecordCursor();
        queryRecords(tagId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> list) {
                        mRecordList = list;
                        recordsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadMoreRecords(Integer scrollPosition) {
        int originSize = mRecordList == null ? 0:mRecordList.size();
        queryRecords(mTagId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> list) {
                        moreCursor.number = DEFAULT_LOAD_MORE;
                        mRecordList.addAll(list);
                        moreObserver.setValue(originSize + 1);
                        if (scrollPosition != null) {
                            scrollPositionObserver.setValue(scrollPosition);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<RecordProxy>> queryRecords(long tagId) {
        return getComplexFilter(mSortMode, mSortDesc, tagId, mRecommendBean, 0)
                .flatMap(filter -> repository.getRecords(filter))
                .flatMap(list -> {
                    moreCursor.offset += list.size();
                    return toViewItems(list);
                });
    }

    private Observable<RecordComplexFilter> getComplexFilter(int sortMode, boolean desc
            , long tagId, RecommendBean filterBean, Integer mRecordType) {
        return Observable.create(e -> {
            RecordComplexFilter filter = new RecordComplexFilter();
            filter.setTagId(tagId);
            filter.setCursor(moreCursor);
            filter.setSortType(sortMode);
            filter.setDesc(desc);
            filter.setRecordType(mRecordType);
            filter.setFilter(filterBean);

            e.onNext(filter);
        });
    }

    private ObservableSource<List<RecordProxy>> toViewItems(List<Record> list) {
        return observer -> {
            List<RecordProxy> results = new ArrayList<>();
            for (Record record:list) {
                RecordProxy proxy = new RecordProxy();
                proxy.setRecord(record);
                proxy.setImagePath(ImageProvider.getRecordRandomPath(record.getName(), null));
                results.add(proxy);
            }
            observer.onNext(results);
        };
    }

}
