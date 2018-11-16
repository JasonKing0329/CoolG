package com.king.app.coolg.phone.video.player;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.phone.video.PlayItemViewBean;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.PlayDuration;
import com.king.app.gdb.data.entity.PlayItem;

import java.util.ArrayList;
import java.util.List;

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

    public MutableLiveData<Integer> playIndexObserver = new MutableLiveData<>();

    private PlayRepository repository;
    
    private List<PlayItemViewBean> mPlayList;
    
    private PlayItemViewBean mPlayBean;
    
    private PlayDuration mPlayDuration;
    
    private int mPlayIndex;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayRepository();
    }

    public void loadPlayItems(long orderId) {
        loadingObserver.setValue(true);
        repository.getPlayItems(orderId)
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
                        mPlayList = playItems;
                        playNext();
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

    public void playNext() {
        if (ListUtil.isEmpty(mPlayList)) {
            messageObserver.setValue("No video");
            return;
        }
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

        playVideoAt(mPlayIndex);
    }

    public void playPrevious() {
        if (ListUtil.isEmpty(mPlayList)) {
            messageObserver.setValue("No video");
            return;
        }

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

        playVideoAt(mPlayIndex);
    }

    public void playVideoAt(int position) {
        mPlayIndex = position;
        mPlayBean = mPlayList.get(mPlayIndex);
        loadPlayDuration(mPlayBean);
        DebugLog.e("play " + mPlayBean.getPlayItem().getRecord().getName());
        videoObserver.setValue(mPlayBean);
        playIndexObserver.setValue(mPlayIndex);
    }

    private ObservableSource<List<PlayItemViewBean>> toViewItems(List<PlayItem> items) {
        return observer -> {
            List<PlayItemViewBean> list = new ArrayList<>();
            for (PlayItem item:items) {
                PlayItemViewBean bean = new PlayItemViewBean();
                bean.setPlayItem(item);
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
        mPlayDuration = repository.getDurationInstance(bean.getPlayItem().getRecordId());
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
}
