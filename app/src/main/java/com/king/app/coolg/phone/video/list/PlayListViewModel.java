package com.king.app.coolg.phone.video.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.coolg.view.widget.video.UrlCallback;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayOrder;

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
 * @date: 2018/11/15 14:32
 */
public class PlayListViewModel extends BaseViewModel {

    public MutableLiveData<List<PlayItemViewBean>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<PlayOrder> orderObserver = new MutableLiveData<>();

    public ObservableField<String> totalText = new ObservableField<>();

    public ObservableField<String> actionbarTitleText = new ObservableField<>();

    private PlayRepository repository;

    private long mOrderId;

    public PlayListViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayRepository();
        updateTotalText("");
    }

    private void updateTotalText(String total) {
        String actionText = "Play List";
        if (ScreenUtils.isTablet() && !TextUtils.isEmpty(total)) {
            actionText = "Play List (" + total + ")";
        }
        actionbarTitleText.set(actionText);
        totalText.set(total);
    }

    public void loadPlayItems(long orderId) {
        mOrderId = orderId;
        loadingObserver.setValue(true);
        loadOrder(orderId)
                .flatMap(order -> {
                    orderObserver.postValue(order);
                    return repository.getPlayItems(orderId);
                })
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlayItemViewBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlayItemViewBean> playItems) {
                        loadingObserver.setValue(false);
                        itemsObserver.setValue(playItems);
                        updateTotalText(playItems.size() + " Videos");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<PlayOrder> loadOrder(long orderId) {
        return Observable.create(e -> e.onNext(getDaoSession().getPlayOrderDao().load(orderId)));
    }

    private ObservableSource<List<PlayItemViewBean>> toViewItems(List<PlayItem> items) {
        return observer -> {
            List<PlayItemViewBean> list = new ArrayList<>();
            for (PlayItem item:items) {
                PlayItemViewBean bean = new PlayItemViewBean();
                bean.setPlayItem(item);
                bean.setRecord(item.getRecord());
                bean.setPlayUrl(item.getUrl());
                if (item.getRecord() != null) {
                    String cover = ImageProvider.getRecordRandomPath(item.getRecord().getName(), null);
                    bean.setCover(cover);
                }
                list.add(bean);
            }
            observer.onNext(list);
        };
    }

    public void deleteItem(int position) {
        loadingObserver.setValue(true);
        repository.deletePlayItem(mOrderId, itemsObserver.getValue().get(position).getPlayItem().getRecordId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        loadingObserver.setValue(false);
                        itemsObserver.getValue().remove(position);
                        itemsObserver.setValue(itemsObserver.getValue());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void clearOrder() {
        repository.deleteAllPlayItems(mOrderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        loadingObserver.setValue(false);
                        itemsObserver.setValue(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getPlayUrl(int position, UrlCallback callback) {
        PlayItemViewBean bean = itemsObserver.getValue().get(position);
        PathRequest request = new PathRequest();
        request.setName(bean.getRecord().getName());
        request.setPath(bean.getRecord().getDirectory());
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().getVideoPath(request)
                .flatMap(response -> UrlUtil.toVideoUrl(response))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String url) {
                        loadingObserver.setValue(false);
                        itemsObserver.getValue().get(position).setPlayUrl(url);
                        callback.onReceiveUrl(url);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
