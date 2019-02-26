package com.king.app.coolg.phone.video.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.repository.PlayRepository;
import com.king.app.coolg.phone.video.home.VideoGuy;
import com.king.app.gdb.data.entity.VideoCoverStar;
import com.king.app.gdb.data.entity.VideoCoverStarDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * @date: 2019/2/25 13:39
 */
public class PopularStarViewModel extends BaseViewModel {

    public MutableLiveData<List<VideoGuy>> starsObserver = new MutableLiveData<>();
    
    private PlayRepository playRepository;
    
    public PopularStarViewModel(@NonNull Application application) {
        super(application);
        playRepository = new PlayRepository();
    }
    
    public void loadStars() {
        loadingObserver.setValue(true);
        getStars()
                .flatMap(stars -> toViewItems(stars))
                .flatMap(stars -> sort(stars))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<VideoGuy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<VideoGuy> videoGuys) {
                        loadingObserver.setValue(false);
                        starsObserver.setValue(videoGuys);
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
    
    private Observable<List<VideoCoverStar>> getStars() {
        return Observable.create(e -> e.onNext(getDaoSession().getVideoCoverStarDao().loadAll()));
    }
    
    private ObservableSource<List<VideoGuy>> toViewItems(List<VideoCoverStar> list) {
        return observer -> {
            List<VideoGuy> guys = new ArrayList<>();
            for (VideoCoverStar star:list) {
                VideoGuy guy = new VideoGuy();
                guy.setVisibility(View.GONE);
                guy.setStar(star.getStar());
                guy.setImageUrl(ImageProvider.getStarRandomPath(star.getStar().getName(), null));
                guy.setVideos(playRepository.getNotDeprecatedCount(star.getStarId()));
                guys.add(guy);
            }
            observer.onNext(guys);
        };
    }

    private ObservableSource<List<VideoGuy>> sort(List<VideoGuy> list) {
        return observer -> {
            Collections.sort(list, new NameComparator());
            observer.onNext(list);
        };
    }

    private class NameComparator implements Comparator<VideoGuy> {

        @Override
        public int compare(VideoGuy left, VideoGuy right) {
            return left.getStar().getName().toLowerCase().compareTo(right.getStar().getName().toLowerCase());
        }
    }

    public void executeDelete() {
        if (starsObserver.getValue() != null) {
            for (VideoGuy item:starsObserver.getValue()) {
                if (item.isChecked()) {
                    getDaoSession().getVideoCoverStarDao().queryBuilder()
                            .where(VideoCoverStarDao.Properties.StarId.eq(item.getStar().getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
            }
            getDaoSession().getVideoCoverStarDao().detachAll();
            loadStars();
        }
    }

    public void insertVideoCoverStar(ArrayList<CharSequence> list)  {
        insertCoverGuys(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean pass) {
                        loadStars();
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

    private Observable<Boolean> insertCoverGuys(ArrayList<CharSequence> list) {
        return Observable.create(e -> {
            List<VideoCoverStar> insertList = new ArrayList<>();
            for (CharSequence str:list) {
                long starId = Long.parseLong(str.toString());
                // insert if not exist
                long count = getDaoSession().getVideoCoverStarDao().queryBuilder()
                        .where(VideoCoverStarDao.Properties.StarId.eq(starId))
                        .buildCount().count();
                if (count == 0) {
                    VideoCoverStar coverStar = new VideoCoverStar();
                    coverStar.setStarId(starId);
                    insertList.add(coverStar);
                }
            }
            getDaoSession().getVideoCoverStarDao().insertInTx(insertList);
            getDaoSession().getVideoCoverStarDao().detachAll();
            e.onNext(true);
        });
    }

}
