package com.king.app.coolg.phone.record.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.VideoModel;
import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;

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
 * @date: 2018/8/10 16:44
 */
public class RecordListViewModel extends BaseViewModel {

    private RecordRepository repository;

    private int DEFAULT_LOAD_MORE = 20;

    private RecordCursor moreCursor;

    public MutableLiveData<List<RecordProxy>> recordsObserver = new MutableLiveData<>();
    public MutableLiveData<List<RecordProxy>> moreObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> countObserver = new MutableLiveData<>();

    private int mSortMode;
    private boolean mSortDesc;
    private RecordListFilterBean mFilter;
    private int mRecordType;
    private String mKeyScene;
    private String mKeyword;
    private boolean mShowCanBePlayed;

    public RecordListViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        mSortMode = SettingProperty.getRecordOrderMode();
        mSortDesc = SettingProperty.isRecordOrderModeDesc();
    }

    public int getSortMode() {
        return mSortMode;
    }

    public void onSortTypeChanged() {
        mSortMode = SettingProperty.getRecordOrderMode();
        mSortDesc = SettingProperty.isRecordOrderModeDesc();
    }

    public void setFilter(RecordListFilterBean mFilter) {
        this.mFilter = mFilter;
    }

    public void setRecordType(int mRecordType) {
        this.mRecordType = mRecordType;
    }

    public int getRecordType() {
        return mRecordType;
    }

    public void setKeyScene(String mKeyScene) {
        this.mKeyScene = mKeyScene;
    }

    public void setKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    public void setShowCanBePlayed(boolean mShowCanBePlayed) {
        this.mShowCanBePlayed = mShowCanBePlayed;
    }

    public boolean isShowCanBePlayed() {
        return mShowCanBePlayed;
    }

    public void newRecordCursor() {
        moreCursor = new RecordCursor();
        moreCursor.number = DEFAULT_LOAD_MORE;
    }

    public void updateDefaultLoad(int number) {
        DEFAULT_LOAD_MORE = number;
    }

    public void loadRecordList() {
        // 偏移量从0开始
        newRecordCursor();
        // 统计全部数量
        countRecords();
        // 查询前N条数据
        queryRecords(mSortMode, mSortDesc, mShowCanBePlayed, mKeyword, mKeyScene, mFilter, mRecordType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> list) {
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

    private void countRecords() {
        getComplexFilter(mSortMode, mSortDesc, mShowCanBePlayed, mKeyword, mKeyScene, mFilter, mRecordType)
                .flatMap(filter -> repository.getRecordCount(filter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Long count) {
                        countObserver.setValue(count.intValue());
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

    public void loadMoreRecords() {
        queryRecords(mSortMode, mSortDesc, mShowCanBePlayed, mKeyword, mKeyScene, mFilter, mRecordType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> list) {
                        moreObserver.setValue(list);
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

    /**
     *
     * @param sortMode sort type
     * @param desc sort desc
     * @param showCanBePlayed records can be played in device(has video resource in specific path)
     * @param nameLike record name contains nameLike
     * @param whereScene the scene of record
     * @param filterBean bareback, inner c, NR params of record
     * @param mRecordType record type, 0 stands for all
     * @return
     */
    private Observable<List<RecordProxy>> queryRecords(final int sortMode, final boolean desc
            , final boolean showCanBePlayed, final String nameLike, final String whereScene, final RecordListFilterBean filterBean, final int mRecordType) {
        return getComplexFilter(sortMode, desc, showCanBePlayed, nameLike, whereScene, filterBean, mRecordType)
                .flatMap(filter -> repository.getRecords(filter))
                .flatMap(list -> {
                    moreCursor.offset += list.size();
                    return pickCanBePlayedRecord(showCanBePlayed, list);
                })
                .flatMap(list -> toViewItems(list));
    }
    
    private Observable<RecordComplexFilter> getComplexFilter(int sortMode, boolean desc
            , boolean showCanBePlayed, String like, String whereScene, RecordListFilterBean filterBean, int mRecordType) {
        return Observable.create(e -> {
            RecordComplexFilter filter = new RecordComplexFilter();
            // 加载可播放的需要从全部记录里通过对比video目录文件信息来挑选
            if (showCanBePlayed) {
                moreCursor.offset = -1;
                moreCursor.number = -1;
            }

            String scene = whereScene;
            if (AppConstants.KEY_SCENE_ALL.equals(whereScene)) {
                scene = null;
            }
            filter.setScene(scene);
            filter.setCursor(moreCursor);
            filter.setSortType(sortMode);
            filter.setDesc(desc);
            filter.setNameLike(like);
            filter.setRecordType(mRecordType);
            filter.setFilter(filterBean);

            e.onNext(filter);
        });
    }

    private ObservableSource<List<Record>> pickCanBePlayedRecord(boolean showCanBePlayed, List<Record> list) {
        return observer -> {
            if (showCanBePlayed) {
                List<Record> rList = new ArrayList<>();
                for (Record record:list) {
                    if (VideoModel.getVideoPath(record.getName()) != null) {
                        rList.add(record);
                    }
                }
                observer.onNext(rList);
            }
            else {
                observer.onNext(list);
            }
        };
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
