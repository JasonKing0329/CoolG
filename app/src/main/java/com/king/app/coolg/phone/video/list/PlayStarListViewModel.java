package com.king.app.coolg.phone.video.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.coolg.view.widget.video.UrlCallback;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/25 11:32
 */
public class PlayStarListViewModel extends BaseViewModel {

    public MutableLiveData<List<PlayItemViewBean>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<Star> starObserver = new MutableLiveData<>();

    public ObservableField<String> totalText = new ObservableField<>();

    private PlayRepository repository;

    private long mStarId;

    public PlayStarListViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayRepository();
    }

    public void loadPlayItems(long starId) {
        mStarId = starId;
        loadingObserver.setValue(true);
        loadStar(starId)
                .flatMap(order -> {
                    starObserver.postValue(order);
                    return repository.getStarPlayItems(starId);
                })
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
                        totalText.set(playItems.size() + " Videos");
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

    private Observable<Star> loadStar(long starId) {
        return Observable.create(e -> e.onNext(getDaoSession().getStarDao().load(starId)));
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
