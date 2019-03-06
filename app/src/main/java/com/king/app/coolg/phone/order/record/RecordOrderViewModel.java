package com.king.app.coolg.phone.order.record;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
 * @date: 2018/8/14 13:24
 */
public class RecordOrderViewModel extends BaseViewModel {

    private FavorRecordOrder mParent;
    private int mPathDepth;
    private Map<Long, Boolean> mOrderCheckMap;
    private Map<Long, Boolean> mItemCheckMap;
    private int mSortType;
    private OrderRepository repository;

    public MutableLiveData<List<OrderItem<FavorRecordOrder>>> orderObserver = new MutableLiveData<>();
    public MutableLiveData<List<RecordProxy>> recordItemsObserver = new MutableLiveData<>();

    public RecordOrderViewModel(@NonNull Application application) {
        super(application);
        mParent = null;
        mPathDepth = 0;
        mOrderCheckMap = new HashMap<>();
        mItemCheckMap = new HashMap<>();
        mSortType = SettingProperty.getPhoneOrderSortType();
        repository = new OrderRepository();
    }

    public Map<Long, Boolean> getOrderCheckMap() {
        return mOrderCheckMap;
    }

    public Map<Long, Boolean> getItemCheckMap() {
        return mItemCheckMap;
    }

    public void loadOrders() {
        loadOrdersFrom(mParent);
    }

    public void loadOrdersFrom(FavorRecordOrder parent) {
        mParent = parent;
        if (parent != null) {
            increaseDepth();
        }
        repository.getRecordOrders(parent, mSortType)
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<OrderItem<FavorRecordOrder>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<OrderItem<FavorRecordOrder>> list) {
                        orderObserver.setValue(list);
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

    private Observable<List<FavorRecordOrder>> getOrders(FavorRecordOrder parent) {
        return Observable.create(e -> {
            List<FavorRecordOrder> list = getDaoSession().getFavorRecordOrderDao().queryBuilder()
                    .where(FavorRecordOrderDao.Properties.ParentId.eq(parent == null ? 0:parent.getId()))
                    .build().list();
            e.onNext(list);
        });
    }

    private ObservableSource<List<OrderItem<FavorRecordOrder>>> toViewItems(List<FavorRecordOrder> list) {
        return observer -> {
            List<OrderItem<FavorRecordOrder>> results = new ArrayList<>();
            for (FavorRecordOrder order:list) {
                OrderItem<FavorRecordOrder> item = new OrderItem<>();
                item.setBean(order);
                item.setId(order.getId());
                item.setName(order.getName());
                if (ListUtil.isEmpty(order.getChildList())) {
                    item.setNumber(order.getRecordList().size());
                }
                else {
                    item.setHasChild(true);
                    item.setNumber(order.getChildList().size());
                }
                item.setImagePath(ImageProvider.parseCoverUrl(order.getCoverUrl()));
                results.add(item);
            }
            observer.onNext(results);
        };
    }

    public void addNewOrder(String name) {
        addNewOrder(name, mParent == null ? 0:mParent.getId());
    }

    public void addNewOrder(String name, long parentId) {
        FavorRecordOrder existedOrder = getDaoSession().getFavorRecordOrderDao().queryBuilder()
                .where(FavorRecordOrderDao.Properties.Name.eq(name))
                .where(FavorRecordOrderDao.Properties.ParentId.eq(parentId))
                .build().unique();
        if (existedOrder != null) {
            messageObserver.setValue(name + " is already existed");
            return;
        }
        FavorRecordOrder order = new FavorRecordOrder();
        order.setName(name);
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        order.setParentId(parentId);
        getDaoSession().getFavorRecordOrderDao().insert(order);
        messageObserver.setValue("Create successfully");
        // 重新加载要刷新
        getDaoSession().getFavorRecordOrderDao().detachAll();
    }

    public void editOrder(FavorRecordOrder order, String name) {
        order.setName(name);
        order.setUpdateTime(new Date());
        getDaoSession().getFavorRecordOrderDao().update(order);
    }

    public void deleteOrder(long orderId) {
        // remove from favor_record
        getDaoSession().getFavorRecordDao().queryBuilder()
                .where(FavorRecordDao.Properties.OrderId.eq(orderId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorRecordDao().detachAll();
        // remove from favor_record_order
        getDaoSession().getFavorRecordOrderDao().queryBuilder()
                .where(FavorRecordOrderDao.Properties.ParentId.eq(orderId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorRecordOrderDao().deleteByKey(orderId);
        getDaoSession().getFavorRecordOrderDao().detachAll();
    }

    public void deleteOrder(List<FavorRecordOrder> list) {
        for (FavorRecordOrder order:list) {
            deleteOrder(order.getId());
        }
    }

    public void deleteSelectedItems(boolean isOrderItem) {
        if (isOrderItem) {
            Iterator<Long> it = mItemCheckMap.keySet().iterator();
            while (it.hasNext()) {
                deleteOrderItem(mParent, it.next());
            }
        }
        else {
            Iterator<Long> it = mOrderCheckMap.keySet().iterator();
            while (it.hasNext()) {
                deleteOrder(it.next());
            }
        }
    }

    private void deleteOrderItem(FavorRecordOrder parent, long recordId) {
        // delete from favor_record
        getDaoSession().getFavorRecordDao().queryBuilder()
                .where(FavorRecordDao.Properties.OrderId.eq(parent.getId()))
                .where(FavorRecordDao.Properties.RecordId.eq(recordId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorStarDao().detachAll();
        // decrease number from favor_record_order
        parent.setNumber(parent.getNumber() - 1);
        getDaoSession().getFavorRecordOrderDao().update(parent);
        getDaoSession().getFavorRecordOrderDao().detach(parent);
    }

    public void backToDepth(int depth) {
        while (mParent != null) {
            mParent = mParent.getParent();
            decreaseDepth();
            if (mPathDepth == depth) {
                if (mPathDepth > 0) {
                    // loadOrders会increaseDepth，所以这里要再一次decrease
                    decreaseDepth();
                }
                break;
            }
        }
        loadOrders();
    }

    public void backToParent() {
        if (mParent != null) {
            mParent = mParent.getParent();
            decreaseDepth();
            loadOrders();
        }
    }

    private void increaseDepth() {
        mPathDepth ++;
    }

    private void decreaseDepth() {
        mPathDepth --;
    }

    public void onSortTypeChanged() {
        mSortType = SettingProperty.getPhoneOrderSortType();
        loadOrders();
    }

    public void loadRecordItems() {
        loadRecordItems(mParent);
    }

    public void loadRecordItems(FavorRecordOrder parent) {
        mParent = parent;
        increaseDepth();
        loadingObserver.setValue(true);
        repository.getFavorRecords(parent.getId(), mSortType)
                .flatMap(list -> toRecordItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RecordProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RecordProxy> list) {
                        loadingObserver.setValue(false);
                        recordItemsObserver.setValue(list);
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

    private ObservableSource<List<RecordProxy>> toRecordItems(List<FavorRecord> list) {
        return observer -> {
            List<RecordProxy> recordList = new ArrayList<>();
            for (FavorRecord record:list) {
                RecordProxy proxy = new RecordProxy();
                proxy.setRecord(record.getRecord());
                proxy.setImagePath(ImageProvider.getRecordRandomPath(record.getRecord().getName(), null));
                recordList.add(proxy);
            }
            if (mSortType == PreferenceValue.PHONE_ORDER_SORT_BY_NAME) {
                Collections.sort(recordList, new RecordNameComparator());
            }
            observer.onNext(recordList);
        };
    }

    public void updateCover(FavorRecordOrder bean, String coverPath) {
        bean.setCoverUrl(coverPath);
        getDaoSession().getFavorRecordOrderDao().update(bean);
        getDaoSession().getFavorRecordOrderDao().detach(bean);
    }

    private class RecordNameComparator implements Comparator<RecordProxy> {

        @Override
        public int compare(RecordProxy l, RecordProxy r) {
            if (l == null || r == null) {
                return 0;
            }

            return l.getRecord().getName().toLowerCase().compareTo(r.getRecord().getName().toLowerCase());
        }
    }
}
