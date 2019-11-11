package com.king.app.coolg.phone.video.player;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.net.rtp.RtpStream;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.coolg.view.widget.video.UrlCallback;
import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
 * @date: 2018/11/15 16:36
 */
public class PlayerViewModel extends BaseViewModel {

    public MutableLiveData<List<PlayItemViewBean>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<PlayItemViewBean> videoObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> closeListObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> stopVideoObserver = new MutableLiveData<>();

    public MutableLiveData<Integer> playIndexObserver = new MutableLiveData<>();

    private PlayRepository repository;
    
    private List<PlayItemViewBean> mPlayList;

    private PlayItemViewBean mPlayBean;
    
    private PlayDuration mPlayDuration;
    
    private int mPlayIndex;

    private boolean isRandomPlay;

    private long mOrderId;

    private long mStarId;

    private RandomPlay randomPlay;

    private Random random;

    private String mSinglePlayUrl;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayRepository();
        randomPlay = new RandomPlay();
        random = new Random();
    }

    private Observable<List<PlayItemViewBean>> getObservable() {
        if (mSinglePlayUrl == null) {
            if (mOrderId != 0) {
                return repository.getPlayItems(mOrderId)
                        .flatMap(list -> toViewItems(list));
            }
            else {
                return repository.getStarPlayItems(mStarId);
            }
        }
        else {
            return getSinglePlay();
        }
    }

    private Observable<List<PlayItemViewBean>> getSinglePlay() {
        return Observable.create(e -> {
            List<PlayItemViewBean> list = new ArrayList<>();
            PlayItemViewBean bean = new PlayItemViewBean();
            bean.setName(mSinglePlayUrl);
            bean.setPlayUrl(mSinglePlayUrl);
            list.add(bean);
            e.onNext(list);
        });
    }

    public void loadPlayItems(long orderId, long starId, boolean random, boolean playLast, String singlePlayUrl) {
        mOrderId = orderId;
        mStarId = starId;
        isRandomPlay = random;
        loadingObserver.setValue(true);
        mSinglePlayUrl = singlePlayUrl;
        getObservable()
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
                        mPlayList = playItems;

                        if (ListUtil.isEmpty(mPlayList)) {
                            messageObserver.setValue("No video");
                        }
                        else {
                            if (playLast) {
                                randomPlay.current = playItems.size() - 1;
                                playVideoAt(playItems.size() - 1);
                            }
                            else {
                                randomPlay.current = 0;
                                playVideoAt(0);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(true);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Boolean isPlayOrder() {
        return mOrderId != 0;
    }

    public String getVideoName(PlayItemViewBean bean) {
        if (bean.getRecord() == null) {
            if (mSinglePlayUrl != null) {
                String[] arr = mSinglePlayUrl.split("/");
                return arr[arr.length - 1];
            }
            return null;
        }
        else {
            return bean.getRecord().getName();
        }
    }

    /**
     * 随机规则
     * list中随机完一轮才随机新的序列号
     * 保存上一个及下一个随机的序号
     * next-->
     *     >-1，last = current, current = next, next = -1
     *     -1--> last = current, next = -1
     *          remains > 0, current = new random
     *          remains = 0, 新一轮随机，remains充满，current = new random
     * last-->
     *     >-1, next = current, current = last, last = -1
     *     -1--> next = current, last = -1
     *          remains > 0, current = new random
     *          remains = 0, 新一轮随机，remains充满，current = new random
     */
    private class RandomPlay {
        int current;
        int last = -1;
        int next = -1;
        List<Integer> remains;
    }

    public void playNext() {
        if (ListUtil.isEmpty(mPlayList)) {
            messageObserver.setValue("No video");
            return;
        }

        if (isRandomPlay) {
            randomPlay.last = randomPlay.current;
            if (randomPlay.next > -1) {
                randomPlay.current = randomPlay.next;
            }
            else {
                randomPlay.current = getRandomPosition();
            }
            mPlayIndex = randomPlay.current;
            randomPlay.next = -1;
        }
        else {
            if (mPlayIndex + 1 >= mPlayList.size()) {
                messageObserver.setValue("No more videos");
                return;
            }

            if (mPlayBean == null) {
                mPlayIndex = 0;
            }
            else {
                mPlayIndex ++;
            }
        }

        playVideoAt(mPlayIndex);
    }

    private int getRandomPosition() {
        if (ListUtil.isEmpty(randomPlay.remains)) {
            randomPlay.remains = new ArrayList<>();
            for (int i = 0; i < mPlayList.size(); i ++) {
                randomPlay.remains.add(i);
            }
        }
        if (randomPlay.remains.size() == 1) {
            return randomPlay.remains.get(0);
        }
        else {
            int index = Math.abs(random.nextInt()) % randomPlay.remains.size();
            int position = randomPlay.remains.get(index);
            randomPlay.remains.remove(index);
            return position;
        }
    }

    public void playPrevious() {
        if (ListUtil.isEmpty(mPlayList)) {
            messageObserver.setValue("No video");
            return;
        }

        if (isRandomPlay) {
            randomPlay.next = randomPlay.current;
            if (randomPlay.last > -1) {
                randomPlay.current = randomPlay.last;
            }
            else {
                randomPlay.current = getRandomPosition();
            }
            mPlayIndex = randomPlay.current;
            randomPlay.last = -1;
        }
        else {
            if (mPlayBean == null) {
                mPlayIndex = 0;
            }
            else {
                mPlayIndex --;
            }
            if (mPlayIndex < 0) {
                messageObserver.setValue("No more videos");
                return;
            }
        }

        playVideoAt(mPlayIndex);
    }

    public int getPlayIndex() {
        return mPlayIndex;
    }

    public void playVideoAt(int position) {
        mPlayIndex = position;
        mPlayBean = mPlayList.get(mPlayIndex);
        loadPlayDuration(mPlayBean);
        if (mSinglePlayUrl == null) {
            DebugLog.e("play " + mPlayBean.getRecord().getName());
        }
        else {
            DebugLog.e("play " + mSinglePlayUrl);
        }

        // 播放star列表，url均未加载过
        if (mPlayBean.getPlayUrl() == null) {
            loadPlayUrl(null);
        }
        else {
            videoObserver.setValue(mPlayBean);
        }
        playIndexObserver.setValue(mPlayIndex);
    }

    public void loadPlayUrl(UrlCallback callback) {
        if (mPlayBean == null) {
            return;
        }
        PathRequest request = new PathRequest();
        request.setName(mPlayBean.getRecord().getName());
        request.setPath(mPlayBean.getRecord().getDirectory());
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
                        mPlayBean.setPlayUrl(url);
                        if (callback == null) {
                            videoObserver.setValue(mPlayBean);
                        }
                        else {
                            callback.onReceiveUrl(url);
                        }
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

    public int getStartSeek() {
        if (mPlayDuration != null) {
            return mPlayDuration.getDuration();
        }
        return 0;
    }
    
    private void loadPlayDuration(PlayItemViewBean bean) {
        if (bean.getRecord() != null) {
            mPlayDuration = repository.getDurationInstance(bean.getRecord().getId());
        }
    }

    public void updatePlayPosition(int currentPosition) {
        if (mPlayDuration != null) {
            mPlayDuration.setDuration(currentPosition);
        }
    }

    public void resetPlayInDb() {
        if (mPlayDuration == null) {
            return;
        }
        repository.deleteDuration(mPlayDuration.getRecordId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

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

    public void updatePlayToDb() {
        if (mPlayDuration != null) {
            DebugLog.e("duration=" + mPlayDuration.getDuration());
            getDaoSession().getPlayDurationDao().insertOrReplace(mPlayDuration);
            getDaoSession().getPlayDurationDao().detachAll();
        }
    }

    public void deletePlayItem(int position, PlayItem item) {
        getDaoSession().getPlayItemDao().delete(item);
        getDaoSession().getPlayItemDao().detachAll();
        mPlayList.remove(position);
        if (position == mPlayIndex) {
            stopVideoObserver.setValue(true);
        }
        mPlayIndex --;
        if (mPlayIndex >= 0) {
            playIndexObserver.setValue(mPlayIndex);
        }
    }
}
