package com.king.app.coolg.phone.star.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.index.StarIndexEmitter;
import com.king.app.coolg.phone.star.category.SelectObserver;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StarSelectorViewModel extends BaseViewModel {

    public MutableLiveData<List<StarProxy>> starsObserver = new MutableLiveData<>();

    public MutableLiveData<String> indexObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> indexBarObserver = new MutableLiveData<>();

    private StarIndexEmitter indexEmitter;

    public StarSelectorViewModel(@NonNull Application application) {
        super(application);
        indexEmitter = new StarIndexEmitter();
    }

    public void loadStars() {
        loadingObserver.setValue(true);
        getStars()
                .flatMap(list -> toViewItems(list))
                .flatMap(list -> {
                    starsObserver.postValue(list);
                    return createIndexes();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        indexObserver.setValue(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        loadingObserver.setValue(false);
                        indexBarObserver.setValue(true);
                    }
                });
    }

    private Observable<List<Star>> getStars() {
        return Observable.create(e -> {
            List<Star> stars = getDaoSession().getStarDao().queryBuilder()
                    .orderAsc(StarDao.Properties.Name)
                    .build().list();
            e.onNext(stars);
            e.onComplete();
        });
    }

    private ObservableSource<List<StarProxy>> toViewItems(List<Star> list) {
        return observer -> {
            List<StarProxy> starProxies = new ArrayList<>();
            for (Star star:list) {
                StarProxy proxy = new StarProxy();
                proxy.setStar(star);
                proxy.setImagePath(ImageProvider.getStarRandomPath(star.getName(), null));
                proxy.setObserver(selectObserver);
                starProxies.add(proxy);
            }
            observer.onNext(starProxies);
            observer.onComplete();
        };
    }

    private Observable<String> createIndexes() {
        return Observable.create(e -> {
            indexEmitter.clear();
            indexEmitter.createNameIndex(e, starsObserver.getValue());
            e.onComplete();
        });
    }

    private SelectObserver<StarProxy> selectObserver = data -> onSelectStar(data);

    private void onSelectStar(StarProxy data) {
        data.setChecked(!data.isChecked());
    }

    public ArrayList<CharSequence> getSelectedItems() {
        ArrayList<CharSequence> list = new ArrayList<>();
        for (StarProxy proxy:starsObserver.getValue()) {
            if (proxy.isChecked()) {
                list.add(String.valueOf(proxy.getStar().getId()));
            }
        }
        return list;
    }

    public int getLetterPosition(String letter) {
        return indexEmitter.getPlayerIndexMap().get(letter).start;
    }

}
