package com.king.app.coolg.phone.order.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.comparator.StarNameComparator;
import com.king.app.coolg.model.repository.OrderRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarDao;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.FavorStarOrderDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private Map<Long, Boolean> mOrderCheckMap;
    private Map<Long, Boolean> mItemCheckMap;
    private int mSortType;
    private OrderRepository repository;

    public MutableLiveData<List<OrderItem<FavorStarOrder>>> orderObserver = new MutableLiveData<>();
    public MutableLiveData<List<StarProxy>> starItemsObserver = new MutableLiveData<>();

    public StarOrderViewModel(@NonNull Application application) {
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

    public void loadOrdersFrom(FavorStarOrder parent) {
        mParent = parent;
        if (parent != null) {
            increaseDepth();
        }
        repository.getStarOrders(parent, mSortType)
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

    private void deleteOrderItem(FavorStarOrder parent, long starId) {
        // delete from favor_star
        getDaoSession().getFavorStarDao().queryBuilder()
                .where(FavorStarDao.Properties.OrderId.eq(parent.getId()))
                .where(FavorStarDao.Properties.StarId.eq(starId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        getDaoSession().getFavorStarDao().detachAll();
        // decrease number from favor_star_order
        parent.setNumber(parent.getNumber() - 1);
        getDaoSession().getFavorStarOrderDao().update(parent);
        getDaoSession().getFavorStarOrderDao().detach(parent);
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

    public void loadStarItems() {
        loadStarItems(mParent);
    }

    public void loadStarItems(FavorStarOrder parent) {
        mParent = parent;
        increaseDepth();
        loadingObserver.setValue(true);
        repository.getFavorStars(parent.getId(), mSortType)
                .flatMap(list -> toStarItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarProxy> list) {
                        loadingObserver.setValue(false);
                        starItemsObserver.setValue(list);
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

    private ObservableSource<List<StarProxy>> toStarItems(List<FavorStar> list) {
        return observer -> {
            List<StarProxy> starList = new ArrayList<>();
            for (FavorStar star:list) {
                StarProxy proxy = new StarProxy();
                proxy.setStar(star.getStar());
                proxy.setImagePath(ImageProvider.getStarRandomPath(star.getStar().getName(), null));
                starList.add(proxy);
            }
            if (mSortType == PreferenceValue.PHONE_ORDER_SORT_BY_NAME) {
                Collections.sort(starList, new StarNameComparator());
            }
            observer.onNext(starList);
        };
    }

    public void updateCover(FavorStarOrder bean, String coverPath) {
        bean.setCoverUrl(coverPath);
        getDaoSession().getFavorStarOrderDao().update(bean);
        getDaoSession().getFavorStarOrderDao().detach(bean);
    }
}
