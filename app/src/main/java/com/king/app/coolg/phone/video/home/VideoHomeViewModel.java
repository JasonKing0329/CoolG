package com.king.app.coolg.phone.video.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
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

    public MutableLiveData<VideoHeadData> headDataObserver = new MutableLiveData<>();

    public VideoHomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void buildPage() {
        loadHeadData();
    }

    private void loadHeadData() {
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
            e.onNext(data);
        });
    }

    public void updateVideoCoverStar(ArrayList<CharSequence> list) {
        updateCoverGuys(list)
                .flatMap(pass -> loadCoverGuys())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<VideoGuy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<VideoGuy> videoGuys) {
                        headDataObserver.getValue().setGuyList(videoGuys);
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

    private Observable<Boolean> updateCoverGuys(ArrayList<CharSequence> list) {
        return Observable.create(e -> {
            getDaoSession().getVideoCoverStarDao().deleteAll();
            List<VideoCoverStar> insertList = new ArrayList<>();
            for (CharSequence str:list) {
                long starId = Long.parseLong(str.toString());
                VideoCoverStar coverStar = new VideoCoverStar();
                coverStar.setStarId(starId);
                insertList.add(coverStar);
            }
            getDaoSession().getVideoCoverStarDao().insertInTx(insertList);
            getDaoSession().getVideoCoverStarDao().detachAll();
            e.onNext(true);
        });
    }

    private ObservableSource<List<VideoGuy>> loadCoverGuys() {
        return observer -> observer.onNext(getCoverGuys());
    }

    private List<VideoGuy> getCoverGuys() {
        List<VideoGuy> guys = new ArrayList<>();
        List<VideoCoverStar> list = getDaoSession().getVideoCoverStarDao().loadAll();
        for (VideoCoverStar cs:list) {
            VideoGuy guy = new VideoGuy();
            guy.setStar(cs.getStar());
        }
        return guys;
    }
}
