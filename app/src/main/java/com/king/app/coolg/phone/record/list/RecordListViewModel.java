package com.king.app.coolg.phone.record.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.VideoModel;
import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
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
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/10 16:44
 */
public class RecordListViewModel extends BaseViewModel {

    private RecordRepository repository;

    protected int DEFAULT_LOAD_MORE = 20;

    private RecordCursor moreCursor;

    public MutableLiveData<List<RecordProxy>> recordsObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> moreObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> countObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> scrollPositionObserver = new MutableLiveData<>();

    private List<RecordProxy> mRecordList;

    private int mSortMode;
    private boolean mSortDesc;
    private RecordListFilterBean mFilter;
    private int mRecordType;
    private String mKeyScene;
    private String mKeyword;
    private boolean mShowCanBePlayed;
    private long mStarId;
    private long mOrderId;
    /**
     * only available when mStarId is not 0
     */
    private int mStarType;
    /**
     * record to be added into order
     */
    private Record mRecordToAdd;

    public RecordListViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        mSortMode = SettingProperty.getRecordSortType();
        mSortDesc = SettingProperty.isRecordSortDesc();
    }

    public int getSortMode() {
        return mSortMode;
    }

    public void onSortTypeChanged() {
        mSortMode = SettingProperty.getRecordSortType();
        mSortDesc = SettingProperty.isRecordSortDesc();
    }

    public void onStarRecordsSortTypeChanged() {
        mSortMode = SettingProperty.getStarRecordsSortType();
        mSortDesc = SettingProperty.isStarRecordsSortDesc();
    }

    public void setDefaultLoadNumber(int defaultLoadNumber) {
        DEFAULT_LOAD_MORE = defaultLoadNumber;
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

    public void setStarId(long starId) {
        this.mStarId = starId;
    }

    public void setOrderId(long mOrderId) {
        this.mOrderId = mOrderId;
    }

    public void setStarType(int type) {
        this.mStarType = type;
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
        queryRecords(mSortMode, mSortDesc, mShowCanBePlayed, mKeyword, mKeyScene, mFilter, mRecordType, mStarId, mOrderId)
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

    private void countRecords() {
        getComplexFilter(mSortMode, mSortDesc, mShowCanBePlayed, mKeyword, mKeyScene, mFilter, mRecordType, mStarId, mOrderId)
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
        loadMoreRecords(null);
    }

    public void loadMoreRecords(Integer scrollPosition) {
        int originSize = mRecordList == null ? 0:mRecordList.size();
        queryRecords(mSortMode, mSortDesc, mShowCanBePlayed, mKeyword, mKeyScene, mFilter, mRecordType, mStarId, mOrderId)
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
    private Observable<List<RecordProxy>> queryRecords(int sortMode, boolean desc, boolean showCanBePlayed
            , String nameLike, String whereScene, RecordListFilterBean filterBean, int mRecordType, long starId, long orderId) {
        return getComplexFilter(sortMode, desc, showCanBePlayed, nameLike, whereScene, filterBean, mRecordType, starId, orderId)
                .flatMap(filter -> repository.getRecords(filter))
                .flatMap(list -> {
                    moreCursor.offset += list.size();
                    return pickCanBePlayedRecord(showCanBePlayed, list);
                })
                .flatMap(list -> pickStarTypeRecord(list))
                .flatMap(list -> toViewItems(list));
    }
    
    private Observable<RecordComplexFilter> getComplexFilter(int sortMode, boolean desc
            , boolean showCanBePlayed, String like, String whereScene, RecordListFilterBean filterBean, int mRecordType, long starId, long orderId) {
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
            filter.setStarId(starId);
            filter.setStudioId(orderId);

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

    private ObservableSource<List<Record>> pickStarTypeRecord(List<Record> list) {
        return observer -> {
            // mStarType is available only when mStarId is not 0
            if (mStarId != 0 && mStarType != 0) {
                List<Record> rList = new ArrayList<>();
                for (Record record:list) {
                    for (RecordStar rs:record.getRelationList()) {
                        if ((rs.getType() == mStarType || rs.getType() == DataConstants.VALUE_RELATION_MIX)
                                && mStarId == rs.getStarId()) {
                            rList.add(record);
                            break;
                        }
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

    public String getBottomText() {
        String filters = getFilterText();
        String sorts = getSortText();
        if (!TextUtils.isEmpty(filters)) {
            return filters + "\n" + sorts;
        }
        else {
            return sorts;
        }
    }

    private String getFilterText() {
        StringBuffer buffer = new StringBuffer();
        if (!TextUtils.isEmpty(mKeyScene) && !AppConstants.KEY_SCENE_ALL.equals(mKeyScene)) {
            buffer.append(", ").append(mKeyScene);
        }
        if (!TextUtils.isEmpty(mKeyword)) {
            buffer.append(", ").append("Keyword[").append(mKeyword).append("]");
        }
        if (mShowCanBePlayed) {
            buffer.append(", ").append("Playable");
        }
        if (mFilter != null) {
            if (mFilter.isBareback()) {
                buffer.append(", ").append("Bareback");
            }
            if (mFilter.isInnerCum()) {
                buffer.append(", ").append("Inner cum");
            }
            if (mFilter.isNotDeprecated()) {
                buffer.append(", ").append("Not Deprecated");
            }
        }
        String text = buffer.toString();
        if (text.length() == 0) {
            return "";
        }
        else {
            if (text.length() > 1) {
                text = text.substring(1);
            }
            return "Filters: " + text;
        }
    }

    private String getSortText() {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Sort by ").append(PreferenceValue.RECORD_SORT_ARRAY[mSortMode]);
            if (mSortDesc) {
                buffer.append(" DESC");
            }
            else {
                buffer.append(" ASC");
            }
            return buffer.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public void markRecordToAdd(Record data) {
        this.mRecordToAdd = data;
    }

    public void addToOrder(long orderId) {
        new OrderRepository().addFavorRecord(orderId, mRecordToAdd.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<FavorRecord>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(FavorRecord favorRecord) {
                        messageObserver.setValue("Add successfully");
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

    public int getOffset() {
        if (moreCursor != null) {
            return moreCursor.offset;
        }
        return 0;
    }

    public void setOffset(int offset) {
        if (moreCursor != null) {
            moreCursor.number = offset - moreCursor.offset + DEFAULT_LOAD_MORE;
            loadMoreRecords(offset);
        }
    }
}
