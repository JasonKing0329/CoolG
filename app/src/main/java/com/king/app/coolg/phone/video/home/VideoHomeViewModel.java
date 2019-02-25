package com.king.app.coolg.phone.video.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.coolg.view.widget.video.EmbedVideoView;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.VideoCoverPlayOrder;
import com.king.app.gdb.data.entity.VideoCoverStar;

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
 * @date: 2019/2/22 15:25
 */
public class VideoHomeViewModel extends BaseViewModel {

    public MutableLiveData<List<VideoItem>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<List<PlayItemViewBean>> recommendObserver = new MutableLiveData<>();

    public MutableLiveData<VideoHeadData> headDataObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> getPlayUrlFailed = new MutableLiveData<>();

    private PlayRepository playRepository;

    public VideoHomeViewModel(@NonNull Application application) {
        super(application);
        playRepository = new PlayRepository();
    }

    public void buildPage() {
        loadHeadData();
        loadRecommend();
    }

    public void loadRecommend() {
        playRepository.getPlayableRecords(5)
                .flatMap(list -> toRecommendItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlayItemViewBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlayItemViewBean> list) {
                        recommendObserver.setValue(list);
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

    private ObservableSource<List<PlayItemViewBean>> toRecommendItems(List<Record> records) {
        return observer -> {
            List<PlayItemViewBean> list = new ArrayList<>();
            for (Record record:records) {
                PlayItemViewBean bean = new PlayItemViewBean();
                bean.setRecord(record);
                bean.setCover(ImageProvider.getRecordRandomPath(record.getName(), null));
                list.add(bean);
            }
            observer.onNext(list);
        };
    }

    public void loadHeadData() {
        getHeadData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<VideoHeadData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(VideoHeadData videoHeadData) {
                        headDataObserver.setValue(videoHeadData);
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

    private Observable<VideoHeadData> getHeadData() {
        return Observable.create(e -> {
            VideoHeadData data = new VideoHeadData();
            data.setGuyList(getCoverGuys());
            data.setPlayLists(getCoverPlayLists());
            e.onNext(data);
        });
    }

    private List<VideoGuy> getCoverGuys() {
        List<VideoGuy> guys = new ArrayList<>();
        // 随机加载最多4个
        List<VideoCoverStar> list = getDaoSession().getVideoCoverStarDao().queryBuilder()
                .orderRaw("RANDOM()")
                .limit(4)
                .build().list();
        for (VideoCoverStar cs:list) {
            VideoGuy guy = new VideoGuy();
            guy.setStar(cs.getStar());
            guy.setImageUrl(ImageProvider.getStarRandomPath(cs.getStar().getName(), null));
            guys.add(guy);
        }
        return guys;
    }

    public void updateVideoCoverPlayList(ArrayList<CharSequence> list) {
        updateCoverPlayList(list)
                .flatMap(pass -> loadCoverPlayLists())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<VideoPlayList>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<VideoPlayList> lists) {
                        headDataObserver.getValue().setPlayLists(lists);
                        headDataObserver.setValue(headDataObserver.getValue());
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

    private Observable<Boolean> updateCoverPlayList(ArrayList<CharSequence> list) {
        return Observable.create(e -> {
            getDaoSession().getVideoCoverPlayOrderDao().deleteAll();
            List<VideoCoverPlayOrder> insertList = new ArrayList<>();
            for (CharSequence str:list) {
                long orderId = Long.parseLong(str.toString());
                VideoCoverPlayOrder order = new VideoCoverPlayOrder();
                order.setOrderId(orderId);
                insertList.add(order);
            }
            getDaoSession().getVideoCoverPlayOrderDao().insertInTx(insertList);
            getDaoSession().getVideoCoverPlayOrderDao().detachAll();
            e.onNext(true);
        });
    }

    private ObservableSource<List<VideoPlayList>> loadCoverPlayLists() {
        return observer -> observer.onNext(getCoverPlayLists());
    }

    private List<VideoPlayList> getCoverPlayLists() {
        List<VideoPlayList> lists = new ArrayList<>();
        List<VideoCoverPlayOrder> list = getDaoSession().getVideoCoverPlayOrderDao().loadAll();
        for (VideoCoverPlayOrder cs:list) {
            VideoPlayList playList = new VideoPlayList();
            playList.setName(cs.getOrder().getName());
            playList.setImageUrl(cs.getOrder().getCoverUrl());
            playList.setPlayOrder(cs.getOrder());
            lists.add(playList);
        }
        return lists;
    }

    public void getRecommendPlayUrl(int position, EmbedVideoView.UrlCallback callback) {
        PlayItemViewBean bean = recommendObserver.getValue().get(position);
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
                        getPlayUrlFailed.setValue(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
