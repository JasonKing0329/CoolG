package com.king.app.coolg.phone.order.record;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;

import java.util.ArrayList;
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
    private Map<Long, Boolean> mCheckMap;

    public MutableLiveData<List<OrderItem<FavorRecordOrder>>> orderObserver = new MutableLiveData<>();

    public RecordOrderViewModel(@NonNull Application application) {
        super(application);
        mParent = null;
        mPathDepth = 0;
        mCheckMap = new HashMap<>();
    }

    public Map<Long, Boolean> getCheckMap() {
        return mCheckMap;
    }

    public void loadOrders() {
        loadOrdersFrom(mParent);
    }

    public void loadOrdersFrom(FavorRecordOrder parent) {
        mParent = parent;
        if (parent != null) {
            mPathDepth ++;
        }
        getOrders(parent)
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
                item.setImagePath(order.getCoverUrl());
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

    public void deleteSelectedItems() {
        Iterator<Long> it = mCheckMap.keySet().iterator();
        while (it.hasNext()) {
            deleteOrder(it.next());
        }
    }

    public void backToDepth(int depth) {
        while (mParent != null) {
            mParent = mParent.getParent();
            mPathDepth --;
            if (mPathDepth == depth) {
                break;
            }
        }
        loadOrders();
    }

    public void backToParent() {
        if (mParent != null) {
            mParent = mParent.getParent();
            loadOrders();
        }
    }
}
