package com.king.app.coolg.pad.video;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.phone.video.home.VideoGuy;
import com.king.app.coolg.phone.video.home.VideoHeadData;
import com.king.app.coolg.phone.video.home.VideoHomeViewModel;
import com.king.app.coolg.phone.video.home.VideoPlayList;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.VideoCoverStar;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VideoHomePadViewModel extends VideoHomeViewModel {

    public MutableLiveData<Boolean> videoPlayOnReadyObserver = new MutableLiveData<>();

    public VideoHomePadViewModel(@NonNull Application application) {
        super(application);
    }

    public void buildPage() {
        loadHeadData();
        loadRecommend();
    }

    public VideoGuy getGuy(int position) {
        try {
            return headDataObserver.getValue().getGuy(position);
        } catch (Exception e) {}
        return null;
    }

    public VideoPlayList getPlayList(int position) {
        try {
            return headDataObserver.getValue().getPlayList(position);
        } catch (Exception e) {}
        return null;
    }

    @Override
    protected void getPadCovers(VideoHeadData data) {
        List<PlayOrder> orders = getDaoSession().getPlayOrderDao().queryBuilder()
                .orderRaw("RANDOM()")
                .limit(1)
                .build().list();
        if (orders.size() > 0) {
            data.setPadPlayListCover(ImageProvider.parseCoverUrl(orders.get(0).getCoverUrl()));
        }
        List<VideoCoverStar> stars = getDaoSession().getVideoCoverStarDao().queryBuilder()
                .orderRaw("RANDOM()")
                .limit(1)
                .build().list();
        if (stars.size() > 0) {
            data.setPadGuyCover(ImageProvider.getStarRandomPath(stars.get(0).getStar().getName(), null));
        }
    }

    public void playItem(PlayItemViewBean item) {
        // 将视频url添加到临时播放列表的末尾
        playRepository.insertToTempList(item.getRecord().getId(), item.getPlayUrl())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PlayItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(PlayItem item) {
                        videoPlayOnReadyObserver.setValue(true);
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
}
