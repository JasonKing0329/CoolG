package com.king.app.coolg.phone.video.player;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.bean.PlayList;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.rx.SimpleObserver;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.UrlUtil;
import com.king.app.coolg.view.widget.video.UrlCallback;
import com.king.app.gdb.data.entity.Record;

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

    public MutableLiveData<List<PlayList.PlayItem>> itemsObserver = new MutableLiveData<>();

    public MutableLiveData<PlayList.PlayItem> playVideo = new MutableLiveData<>();

    public MutableLiveData<PlayList.PlayItem> prepareVideo = new MutableLiveData<>();

    public MutableLiveData<PlayList.PlayItem> videoUrlIsReady = new MutableLiveData<>();

    public MutableLiveData<Boolean> closeListObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> stopVideoObserver = new MutableLiveData<>();

    public MutableLiveData<Integer> playIndexObserver = new MutableLiveData<>();

    public ObservableField<String> playModeText = new ObservableField<>();
    public ObservableField<String> playListText = new ObservableField<>();

    private List<PlayList.PlayItem> mPlayList = new ArrayList<>();

    private PlayList.PlayItem mPlayBean;

    private int mPlayIndex;

    /**
     * 播放列表的播放模式：顺序、随机
     */
    private boolean isRandomPlay;

    /**
     * 自定义条件随机播放模式（随机产生符合条件的record，并且会被加入到播放列表中）
     */
    private boolean isCustomRandomPlay;

    private RandomPlay randomPlay;

    private Random random;

    private RecordRepository recordRepository = new RecordRepository();

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        randomPlay = new RandomPlay();
        random = new Random();
    }

    private void updatePlayModeText() {
        if (isRandomPlay) {
            playModeText.set("随机");
        }
        else {
            playModeText.set("顺序");
        }
    }

    private Observable<List<PlayList.PlayItem>> getObservable() {
        return Observable.create(e -> {
            PlayList playList = PlayListInstance.getInstance().getPlayList();
            isRandomPlay = playList.getPlayMode() == 1;
            updatePlayModeText();
            List<PlayList.PlayItem> list = playList.getList();
            // 加载图片地址
            for (PlayList.PlayItem item:list) {
                Record record = getDaoSession().getRecordDao().load(item.getRecordId());
                if (record != null) {
                    item.setImageUrl(ImageProvider.getRecordRandomPath(record.getName(), null));
                }
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    public void loadPlayItems(boolean autoPlay) {
        loadingObserver.setValue(true);
        getObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlayList.PlayItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlayList.PlayItem> playItems) {
                        loadingObserver.setValue(false);
                        itemsObserver.setValue(playItems);
                        mPlayList = playItems;
                        updatePlayListText();

                        if (ListUtil.isEmpty(mPlayList)) {
                            messageObserver.setValue("No video");
                        }
                        else {
                            int startIndex = PlayListInstance.getInstance().getPlayList().getPlayIndex();
                            randomPlay.current = startIndex;
                            if (autoPlay) {
                                playVideoAt(startIndex);
                            }
                            else {
                                prepareVideoAt(startIndex);
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

    public String getVideoName(PlayList.PlayItem bean) {
        if (TextUtils.isEmpty(bean.getName())) {
            return bean.getUrl();
        }
        else {
            return bean.getName();
        }
    }

    public void clearViewList() {
        mPlayList.clear();
        if (randomPlay.remains != null) {
            randomPlay.remains.clear();
        }
    }

    public void clearAll() {
        // 从UI中删除
        clearViewList();
        // 从播放列表持久化删除
        PlayListInstance.getInstance().clearPlayList();

        itemsObserver.setValue(mPlayList);
    }

    public void switchPlayMode() {
        isRandomPlay = !isRandomPlay;
        updatePlayModeText();
        PlayListInstance.getInstance().updatePlayMode(isRandomPlay ? 1:0);
    }

    public void setIsCustomRandomPlay(boolean enable) {
        isCustomRandomPlay = enable;
        if (isCustomRandomPlay) {
            sendRandomList();
        }
        else {
            if (mPlayList.size() > 0) {
                mPlayIndex = mPlayList.size() - 1;
                playIndexObserver.setValue(mPlayIndex);
                PlayListInstance.getInstance().updatePlayIndex(mPlayIndex);
            }
            itemsObserver.setValue(mPlayList);
        }
    }

    private void sendRandomList() {
        if (mPlayBean == null) {
            return;
        }
        List<PlayList.PlayItem> list = new ArrayList<>();
        list.add(mPlayBean);
        itemsObserver.setValue(list);
    }

    public void updateDuration(int duration) {
        if (mPlayBean != null) {
            mPlayBean.setDuration(duration);
            PlayListInstance.getInstance().updatePlayItem(mPlayBean);
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
        if (isCustomRandomPlay) {
            RecommendBean bean = SettingProperty.getVideoRecBean();
            bean.setOnline(true);
            bean.setNumber(1);
            recordRepository.getRecordsBy(bean)
                    .flatMap(list -> addToPlayList(list))
                    .compose(applySchedulers())
                    .subscribe(new SimpleObserver<PlayList.PlayItem>(compositeDisposable) {
                        @Override
                        public void onNext(PlayList.PlayItem playItem) {
                            updatePlayListText();
                            mPlayBean = playItem;
                            Record record = getDaoSession().getRecordDao().load(mPlayBean.getRecordId());
                            if (record != null) {
                                mPlayBean.setImageUrl(ImageProvider.getRecordRandomPath(record.getName(), null));
                            }
                            sendRandomList();
                            playVideoAt(0);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
        else {
            playNextInList();
        }
    }

    private ObservableSource<PlayList.PlayItem> addToPlayList(List<Record> recordList) {
        return observer -> {
            PlayList.PlayItem item = PlayListInstance.getInstance().addRecord(recordList.get(0), null);
            // 加入到播放列表中
            mPlayList.add(item);
            observer.onNext(item);
            observer.onComplete();
        };
    }

    private void playNextInList() {
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

    private void prepareVideoAt(int position) {
        mPlayIndex = position;
        if (mPlayIndex > itemsObserver.getValue().size()) {
            mPlayIndex = itemsObserver.getValue().size() - 1;
        }
        if (mPlayIndex < 0) {
            return;
        }
        mPlayBean = itemsObserver.getValue().get(mPlayIndex);
        DebugLog.e("play " + mPlayBean.getUrl());
        prepareVideo.setValue(mPlayBean);
        playIndexObserver.setValue(mPlayIndex);
        PlayListInstance.getInstance().updatePlayIndex(mPlayIndex);
    }

    public void playVideoAt(int position) {
        prepareVideoAt(position);
        playVideo.setValue(mPlayBean);
    }

    public void loadPlayUrl(PlayList.PlayItem item) {
        Record record = getDaoSession().getRecordDao().load(item.getRecordId());
        if (record == null) {
            return;
        }
        PathRequest request = new PathRequest();
        request.setName(record.getName());
        request.setPath(record.getDirectory());
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().getVideoPath(request)
                .flatMap(response -> UrlUtil.toVideoUrl(response))
                .compose(applySchedulers())
                .subscribe(new SimpleObserver<String>(compositeDisposable) {
                    @Override
                    public void onNext(String s) {
                        loadingObserver.setValue(false);
                        item.setUrl(s);
                        videoUrlIsReady.setValue(item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }
                });
    }

    /**
     * record最初被加到list中url都是null，采用即时获取url的方法
     * @param callback
     */
    public void loadPlayUrl(UrlCallback callback) {
        if (mPlayBean == null) {
            return;
        }
        Record record = getDaoSession().getRecordDao().load(mPlayBean.getRecordId());
        if (record == null) {
            return;
        }
        PathRequest request = new PathRequest();
        request.setName(record.getName());
        request.setPath(record.getDirectory());
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
                        mPlayBean.setUrl(url);
                        PlayListInstance.getInstance().updatePlayItem(mPlayBean);
                        if (callback == null) {
                            prepareVideo.setValue(mPlayBean);
                            playVideo.setValue(mPlayBean);
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

    public int getStartSeek() {
        if (mPlayBean != null) {
            return mPlayBean.getPlayTime();
        }
        return 0;
    }

    public void updatePlayPosition(int currentPosition) {
        if (mPlayBean != null) {
            mPlayBean.setPlayTime(currentPosition);
        }
    }

    public void resetPlayInDb() {
        if (mPlayBean == null) {
            return;
        }
        mPlayBean.setPlayTime(0);
        PlayListInstance.getInstance().updatePlayItem(mPlayBean);
    }

    public void updatePlayToDb() {
        if (mPlayBean != null) {
            PlayListInstance.getInstance().updatePlayItem(mPlayBean);
        }
    }

    public void deletePlayItem(int position, PlayList.PlayItem item) {
        PlayListInstance.getInstance().deleteItem(item);
        mPlayList.remove(position);
        if (position == mPlayIndex) {
            stopVideoObserver.setValue(true);
        }
        mPlayIndex --;
        if (mPlayIndex >= 0) {
            playIndexObserver.setValue(mPlayIndex);
            PlayListInstance.getInstance().updatePlayIndex(mPlayIndex);
        }
        updatePlayListText();
    }

    private void updatePlayListText() {
        playListText.set("Play List(" + ListUtil.getSize(mPlayList) + ")");
    }

    public void updateRecommend(RecommendBean bean) {
        SettingProperty.setVideoRecBean(bean);
    }

}
