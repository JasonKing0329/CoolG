package com.king.app.coolg.phone.star.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.conf.RatingType;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.comparator.StarNameComparator;
import com.king.app.coolg.model.comparator.StarRatingComparator;
import com.king.app.coolg.model.comparator.StarRecordsNumberComparator;
import com.king.app.coolg.model.index.StarIndexEmitter;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Star;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 14:52
 */
public class StarListViewModel extends BaseViewModel {
    
    private int currentViewMode;

    // see AppConstants.STAR_SORT_XXX
    private int mSortType;

    // see GDBProperties.STAR_MODE_XXX
    private String mStarType;

    private long mStudioId;

    private List<StarProxy> mFullList;

    private List<StarProxy> mList;

    private Map<Long, Boolean> mExpandMap;

    private String mKeyword;

    private StarIndexEmitter indexEmitter;

    private StarRepository repository;

    // 防止重复loading
    private boolean isLoading;

    public MutableLiveData<String> indexObserver = new MutableLiveData<>();
    public MutableLiveData<Boolean> indexBarObserver = new MutableLiveData<>();

    public MutableLiveData<List<StarProxy>> circleListObserver = new MutableLiveData<>();
    public MutableLiveData<List<StarProxy>> richListObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> circleUpdateObserver = new MutableLiveData<>();
    public MutableLiveData<Boolean> richUpdateObserver = new MutableLiveData<>();

    public ObservableInt indexBarVisibility = new ObservableInt();

    public StarListViewModel(@NonNull Application application) {
        super(application);
        if (ScreenUtils.isTablet()) {
            SettingProperty.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_CIRCLE);
            currentViewMode = PreferenceValue.STAR_LIST_VIEW_CIRCLE;
        }
        else {
            SettingProperty.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_RICH);
            currentViewMode = PreferenceValue.STAR_LIST_VIEW_RICH;
        }
        mSortType = AppConstants.SCENE_SORT_NAME;
        mExpandMap = new HashMap<>();
        indexEmitter = new StarIndexEmitter();
        repository = new StarRepository();
    }

    public void setStarType(String mStarType) {
        this.mStarType = mStarType;
    }

    public void setStudioId(long studioId) {
        mStudioId = studioId;
    }

    public String getStarType() {
        return mStarType;
    }

    public void setSortType(int sortType) {
        this.mSortType = sortType;
    }

    public int getSortType() {
        return mSortType;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadStarList() {
        isLoading = true;
        currentViewMode = SettingProperty.getStarListViewMode();
        loadingObserver.setValue(true);
        queryStars()
                .flatMap(list -> toViewItems(list))
                .flatMap(list -> {
                    mFullList = list;
                    mExpandMap.clear();
                    mList = new ArrayList<>();
                    for (StarProxy proxy:mFullList) {
                        mList.add(proxy);
                    }
                    // 默认收起
                    setExpandAll(false);
                    return sortStars();
                })
                .flatMap(list -> createIndexes())
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
                        isLoading = false;
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        isLoading = false;
                        loadingObserver.setValue(false);
                        indexBarObserver.setValue(true);
                        if (mSortType == AppConstants.STAR_SORT_RANDOM) {
                            indexBarVisibility.set(View.GONE);
                        }
                        else {
                            indexBarVisibility.set(View.VISIBLE);
                        }
                        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                            circleListObserver.setValue(mList);
                        }
                        else {
                            richListObserver.setValue(mList);
                        }
                    }
                });
    }

    private Observable<List<Star>> queryStars() {
        if (mStudioId == 0) {
            return repository.queryStar(mStarType);
        }
        else {
            return repository.queryStudioStars(mStudioId, mStarType);
        }
    }

    private Observable<List<StarProxy>> toViewItems(List<Star> list) {
        return Observable.create(e -> {
            // 装配StarProxy
            List<StarProxy> proxyList = new ArrayList<>();
            for (Star star:list) {
                star.getRatings();
                StarProxy proxy = new StarProxy();
                proxy.setStar(star);
                proxy.setImagePath(ImageProvider.getStarRandomPath(star.getName(), null));
                proxyList.add(proxy);
            }
            e.onNext(proxyList);
            e.onComplete();
        });
    }

    private Observable<List<StarProxy>> sortStars() {
        return Observable.create(e -> {
            if (mSortType == AppConstants.STAR_SORT_RANDOM) {// order by records number
                Collections.shuffle(mList);
            }
            else if (mSortType == AppConstants.STAR_SORT_RECORDS) {// order by records number
                Collections.sort(mList, new StarRecordsNumberComparator());
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.COMPLEX));
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING_FACE) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.FACE));
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING_BODY) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.BODY));
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING_DK) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.DK));
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING_SEXUALITY) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.SEXUALITY));
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING_PASSION) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.PASSION));
            }
            else if (mSortType == AppConstants.STAR_SORT_RATING_VIDEO) {// order by rating
                Collections.sort(mList, new StarRatingComparator(RatingType.VIDEO));
            }
            else {
                // order by name
                Collections.sort(mList, new StarNameComparator());
            }
            e.onNext(mList);
            e.onComplete();
        });
    }

    private Observable<String> createIndexes() {
        return Observable.create(e -> {
            indexEmitter.clear();
            switch (mSortType) {
                case AppConstants.SCENE_SORT_NUMBER:
                    indexEmitter.createRecordsIndex(e, mList);
                    break;
                case AppConstants.SCENE_SORT_NAME:
                    indexEmitter.createNameIndex(e, mList);
                    break;
                case AppConstants.STAR_SORT_RANDOM:
                    e.onNext("");
                    break;
                default:
                    indexEmitter.createRatingIndex(e, mList, mSortType);
                    break;
            }
            e.onComplete();
        });
    }

    public int getLetterPosition(String letter) {
        return indexEmitter.getPlayerIndexMap().get(letter).start;
    }

    public String getDetailIndex(int position) {
        return mList.get(position).getStar().getName();
    }

    public Map<Long, Boolean> getExpandMap() {
        return mExpandMap;
    }

    public void setExpandAll(boolean expandAll) {
        mExpandMap.clear();
        for (int i = 0; i < mList.size(); i ++) {
            mExpandMap.put(mList.get(i).getStar().getId(), expandAll);
        }
    }

    public void sortStarList(final int sortType) {
        loadingObserver.setValue(true);
        setSortType(sortType);
        sortStars()
                .flatMap(list -> createIndexes())
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
                        messageObserver.setValue("Sort stars failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        loadingObserver.setValue(false);
                        indexBarObserver.setValue(true);
                        if (mSortType == AppConstants.STAR_SORT_RANDOM) {
                            indexBarVisibility.set(View.GONE);
                        }
                        else {
                            indexBarVisibility.set(View.VISIBLE);
                        }
                        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                            circleUpdateObserver.setValue(true);
                        }
                        else {
                            richUpdateObserver.setValue(true);
                        }
                    }
                });

    }

    public boolean isKeywordChanged(String text) {
        return !text.equals(mKeyword);
    }
    /**
     * filter by inputted text
     * @param text
     */
    public void filter(String text) {
        filterObservable(filterByText(text), false);
    }

    private void filterObservable(Observable<Boolean> observable, boolean showLoading) {
        if (showLoading) {
            loadingObserver.setValue(true);
        }
        observable
                .flatMap(filtered -> sortStars())
                .flatMap(aBoolean -> createIndexes())
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
                        if (showLoading) {
                            loadingObserver.setValue(false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (showLoading) {
                            loadingObserver.setValue(false);
                        }
                        indexBarObserver.setValue(true);
                        if (mSortType == AppConstants.STAR_SORT_RANDOM) {
                            indexBarVisibility.set(View.GONE);
                        }
                        else {
                            indexBarVisibility.set(View.VISIBLE);
                        }
                        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                            circleUpdateObserver.setValue(true);
                        }
                        else {
                            richUpdateObserver.setValue(true);
                        }
                    }
                });
    }

    private Observable<Boolean> filterByText(String text) {
        return Observable.create(e -> {
            mList.clear();
            mKeyword = text;
            for (int i = 0; i < mFullList.size(); i ++) {
                if (TextUtils.isEmpty(text)) {
                    mList.add(mFullList.get(i));
                }
                else {
                    if (isMatchForKeyword(mFullList.get(i), text)) {
                        mList.add(mFullList.get(i));
                    }
                }
            }
            e.onNext(true);
            e.onComplete();
        });
    }

    private boolean isMatchForKeyword(StarProxy starProxy, String text) {
        return starProxy.getStar().getName().toLowerCase().contains(text.toLowerCase());
    }
}
