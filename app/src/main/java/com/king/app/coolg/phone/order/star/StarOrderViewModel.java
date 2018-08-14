package com.king.app.coolg.phone.order.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.FavorStarOrderDao;

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
public class StarOrderViewModel extends BaseViewModel {

    private FavorStarOrder mParent;
    private int mPathDepth;
    private Map<Long, Boolean> mCheckMap;

    public MutableLiveData<List<OrderItem<FavorStarOrder>>> orderObserver = new MutableLiveData<>();

    public StarOrderViewModel(@NonNull Application application) {
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

    public void loadOrdersFrom(FavorStarOrder parent) {
        mParent = parent;
        if (parent != null) {
            mPathDepth ++;
        }
        getOrders(parent)
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<OrderItem<FavorStarOrder>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<OrderItem<FavorStarOrder>> list) {
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

    private Observable<List<FavorStarOrder>> getOrders(FavorStarOrder parent) {
        return Observable.create(e -> {
            List<FavorStarOrder> list = getDaoSession().getFavorStarOrderDao().queryBuilder()
                    .where(FavorStarOrderDao.Properties.ParentId.eq(parent == null ? 0:parent.getId()))
                    .build().list();
            e.onNext(list);
        });
    }

    private ObservableSource<List<OrderItem<FavorStarOrder>>> toViewItems(List<FavorStarOrder> list) {
        return observer -> {
            List<OrderItem<FavorStarOrder>> results = new ArrayList<>();
            for (FavorStarOrder order:list) {
                OrderItem<FavorStarOrder> item = new OrderItem<>();
                item.setBean(order);
                item.setId(order.getId());
                item.setName(order.getName());
                if (ListUtil.isEmpty(order.getChildList())) {
                    item.setNumber(order.getStarList().size());
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
        FavorStarOrder existedOrder = getDaoSession().getFavorStarOrderDao().queryBuilder()
                .where(FavorStarOrderDao.Properties.Name.eq(name))
                .where(FavorStarOrderDao.Properties.ParentId.eq(parentId))
                .build().unique();
        if (existedOrder != null) {
            messageObserver.setValue(name + " is already existed");
            return;
        }
        FavorStarOrder order = new FavorStarOrder();
        order.setName(name);
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        order.setParentId(parentId);
        getDaoSession().getFavorStarOrderDao().insert(order);
        messageObserver.setValue("Create successfully");
        // 重新加载要刷新
        getDaoSession().getFavorStarOrderDao().detachAll();
    }

    public void editOrder(FavorStarOrder order, String name) {
        order.setName(name);
        order.setUpdateTime(new Date());
        getDaoSession().getFavorStarOrderDao().update(order);
    }

    public void deleteOrder(long orderId) {
        // remove from favor_record
        getDaoSession().getFavorStarDao().queryBuilder()
                .where(FavorStarDao.Properties.OrderId.eq(orderId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorStarDao().detachAll();
        // remove from favor_record_order
        getDaoSession().getFavorStarOrderDao().queryBuilder()
                .where(FavorStarOrderDao.Properties.ParentId.eq(orderId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorStarOrderDao().deleteByKey(orderId);
        getDaoSession().getFavorStarOrderDao().detachAll();
    }

    public void deleteOrder(List<FavorStarOrder> list) {
        for (FavorStarOrder order:list) {
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
