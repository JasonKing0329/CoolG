package com.king.app.coolg.phone.video.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.bean.PlayList;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.model.rx.SimpleObserver;
import com.king.app.coolg.phone.video.player.PlayListInstance;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.coolg.view.widget.video.UrlCallback;
import com.king.app.gdb.data.entity.Star;

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
 * @date: 2019/2/25 11:32
 */
public class PlayStarListViewModel extends BaseViewModel {

    public MutableLiveData<List<PlayItemViewBean>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<Star> starObserver = new MutableLiveData<>();

    public ObservableField<String> totalText = new ObservableField<>();

    public ObservableField<String> actionbarTitleText = new ObservableField<>();

    private PlayRepository repository;

    private long mStarId;

    public MutableLiveData<Boolean> videoPlayOnReadyObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> playListCreated = new MutableLiveData<>();

    public PlayStarListViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayRepository();
        updateTotalText("", "");
    }

    private void updateTotalText(String starName, String total) {
        String actionText = starName;
        if (ScreenUtils.isTablet() && !TextUtils.isEmpty(total)) {
            actionText = starName + " (" + total + ")";
        }
        actionbarTitleText.set(actionText);
        totalText.set(total);
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
                        updateTotalText(starObserver.getValue().getName(), playItems.size() + " Videos");
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

    public void playItem(PlayItemViewBean item) {
        // 将视频url添加到临时播放列表的末尾
        PlayListInstance.getInstance().addPlayItemViewBean(item);
        PlayListInstance.getInstance().setPlayIndexAsLast();
        videoPlayOnReadyObserver.setValue(true);
    }

    public void createPlayList(boolean clearCurrent, boolean isRandom, long starId) {
        repository.getStarPlayItems(starId)
                .flatMap(list -> addToPlayList(list, clearCurrent, isRandom))
                .compose(applySchedulers())
                .subscribe(new SimpleObserver<Boolean>(compositeDisposable) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        playListCreated.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }
                });
    }

    private ObservableSource<Boolean> addToPlayList(List<PlayItemViewBean> list, boolean clearCurrent, boolean isRandom) {
        return observer -> {
            PlayList playList = PlayListInstance.getInstance().getPlayList();
            if (clearCurrent) {
                PlayListInstance.getInstance().clearPlayList();
            }
            if (list.size() > 0) {
                PlayListInstance.getInstance().addPlayItems(list);
                // 由于添加时可能进行了去重，playIndex要以url来判断
                for (int i = 0; i < playList.getList().size(); i ++) {
                    PlayList.PlayItem item = playList.getList().get(i);
                    if (item.getUrl() != null && item.getUrl().equals(list.get(0).getPlayUrl())) {
                        playList.setPlayIndex(i);
                        break;
                    }
                }
            }
            observer.onNext(true);
            observer.onComplete();
        };
    }
}
