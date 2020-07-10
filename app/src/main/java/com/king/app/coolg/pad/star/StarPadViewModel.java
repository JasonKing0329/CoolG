package com.king.app.coolg.pad.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.phone.star.StarRelationship;
import com.king.app.coolg.phone.star.StarViewModel;
import com.king.app.gdb.data.entity.StarRating;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/20 16:30
 */
public class StarPadViewModel extends StarViewModel {

    public MutableLiveData<List<String>> imagesObserver = new MutableLiveData<>();
    public MutableLiveData<List<StarRelationship>> relationshipsObserver = new MutableLiveData<>();
    public MutableLiveData<StarRating> ratingObserver = new MutableLiveData<>();

    public StarPadViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void loadStar(long starId) {
        loadingObserver.setValue(true);
        starRepository.getStar(starId)
                .flatMap(star -> {
                    mStar = star;
                    starObserver.postValue(mStar);
                    loadRating();
                    return getStarImages(star);
                })
                .flatMap(list -> {
                    imagesObserver.postValue(list);
                    return getStarTags(mStar);
                })
                .flatMap(list -> {
                    tagList = list;
                    tagsObserver.postValue(list);
                    return getRelationships(mStar);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarRelationship>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarRelationship> list) {
                        loadingObserver.setValue(false);
                        relationshipsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadRating() {
        if (mStar.getRatings().size() > 0) {
            ratingObserver.postValue(mStar.getRatings().get(0));
        }
        else {
            ratingObserver.postValue(null);
        }
    }
}
