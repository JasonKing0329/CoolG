package com.king.app.coolg.phone.record.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.bean.RecordComplexFilter;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.PlayItem;
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
    private Integer mRecordType;
    private String mKeyScene;
    private String mKeyword;
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
    /**
     * record to be added into play order
     */
    private Record mRecordToPlayOrder;

    private RecommendBean mRecommendBean;

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

    public void setFilter(RecommendBean mFilter) {
        this.mRecommendBean = mFilter;
    }

    public void setRecordType(Integer mRecordType) {
        this.mRecordType = mRecordType;
    }

    public Integer getRecordType() {
        return mRecordType;
    }

    public void setKeyScene(String mKeyScene) {
        this.mKeyScene = mKeyScene;
    }

    public void setKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
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
        queryRecords(mSortMode, mSortDesc, mKeyword, mKeyScene, mRecommendBean, mRecordType, mStarId, mOrderId)
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
        getComplexFilter(mSortMode, mSortDesc, mKeyword, mKeyScene, mRecommendBean, mRecordType, mStarId, mOrderId)
                .flatMap(filter -> repository.getRecordCount(filter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Integer count) {
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
        queryRecords(mSortMode, mSortDesc, mKeyword, mKeyScene, mRecommendBean, mRecordType, mStarId, mOrderId)
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
     * @param nameLike record name contains nameLike
     * @param whereScene the scene of record
     * @param filterBean bareback, inner c, NR params of record
     * @param mRecordType record type, 0 stands for all
     * @return
     */
    private Observable<List<RecordProxy>> queryRecords(int sortMode, boolean desc
            , String nameLike, String whereScene, RecommendBean filterBean, Integer mRecordType, long starId, long orderId) {
        return getComplexFilter(sortMode, desc, nameLike, whereScene, filterBean, mRecordType, starId, orderId)
                .flatMap(filter -> repository.getRecords(filter))
                .flatMap(list -> {
                    moreCursor.offset += list.size();
                    return pickStarTypeRecord(list);
                })
                .flatMap(list -> toViewItems(list));
    }
    
    private Observable<RecordComplexFilter> getComplexFilter(int sortMode, boolean desc
            , String like, String whereScene, RecommendBean filterBean, Integer mRecordType, long starId, long orderId) {
        return Observable.create(e -> {
            RecordComplexFilter filter = new RecordComplexFilter();

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
        if (mRecommendBean != null) {
            if (!TextUtils.isEmpty(mRecommendBean.getSql())) {
                buffer.append(", ").append(mRecommendBean.getSql());
            }
            if (mRecommendBean.isOnlyType1v1() && !TextUtils.isEmpty(mRecommendBean.getSql1v1())) {
                buffer.append(", ").append(mRecommendBean.getSql1v1());
            }
            if (mRecommendBean.isOnlyType3w() && !TextUtils.isEmpty(mRecommendBean.getSql3w())) {
                buffer.append(", ").append(mRecommendBean.getSql3w());
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

    public void saveRecordToPlayOrder(Record record) {
        mRecordToPlayOrder = record;
    }

    /**
     * add to temp play order
     * @param list
     */
    public void addToPlay(ArrayList<CharSequence> list) {
        insertToVideoOrders(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean item) {
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

    private Observable<Boolean> insertToVideoOrders(ArrayList<CharSequence> orderIds) {
        return Observable.create(e -> {
            if (!ListUtil.isEmpty(orderIds)) {
                PlayRepository playRepository = new PlayRepository();
                for (CharSequence id:orderIds) {
                    long orderId = Long.parseLong(id.toString());
                    if (playRepository.isExist(orderId, mRecordToPlayOrder.getId())) {
                        continue;
                    }
                    PlayItem item = new PlayItem();
                    item.setOrderId(orderId);
                    item.setRecordId(mRecordToPlayOrder.getId());
                    item.setUrl(null);
                    getDaoSession().getPlayItemDao().insertOrReplace(item);
                }
                getDaoSession().getPlayItemDao().detachAll();
            }
            e.onNext(true);
        });
    }
}
