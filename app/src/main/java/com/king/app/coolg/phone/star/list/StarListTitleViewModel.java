package com.king.app.coolg.phone.star.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 17:04
 */
public class StarListTitleViewModel extends BaseViewModel {

    private StarRepository repository;

    private int mViewMode;

    private int mSortMode;

    public MutableLiveData<String> menuViewModeObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> sortTypeObserver = new MutableLiveData<>();

    public MutableLiveData<List<Integer>> titlesObserver = new MutableLiveData<>();

    public StarListTitleViewModel(@NonNull Application application) {
        super(application);
        repository = new StarRepository();
        mSortMode = AppConstants.STAR_SORT_NAME;
        mViewMode = SettingProperty.getStarListViewMode();
    }

    public void toggleViewMode(Resources resources) {
        String title;
        if (mViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
            SettingProperty.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_RICH);
            title = resources.getString(R.string.menu_view_mode_circle);
        }
        else {
            SettingProperty.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_CIRCLE);
            title = resources.getString(R.string.menu_view_mode_rich);
        }
        menuViewModeObserver.setValue(title);
    }

    public void setSortMode(int sortMode) {
        // 如果是random，每次都有效。否则，只有当排序模式变化才重新排序
        if (sortMode == AppConstants.STAR_SORT_RANDOM || sortMode != mSortMode) {
            mSortMode = sortMode;
            sortTypeObserver.setValue(mSortMode);
        }
    }

    public int getSortMode() {
        return mSortMode;
    }

    @Deprecated
    public void loadTitles() {
        Observable.create((ObservableOnSubscribe<List<Integer>>) e -> {
            List<Integer> countList = new ArrayList<>();
            countList.add((int) repository.queryStarCount(DataConstants.STAR_MODE_ALL));
            countList.add((int) repository.queryStarCount(DataConstants.STAR_MODE_TOP));
            countList.add((int) repository.queryStarCount(DataConstants.STAR_MODE_BOTTOM));
            countList.add((int) repository.queryStarCount(DataConstants.STAR_MODE_HALF));
            e.onNext(countList);
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Integer> list) {
                        titlesObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load title error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Star nextFavorStar() {
        try {
            return repository.getRandomRatingAbove(StarRatingUtil.RATING_VALUE_CP, 1).get(0);
        } catch (Exception e) {}
        return null;
    }

    public String getStudioName(long studioId) {
        FavorRecordOrder studio = getDaoSession().getFavorRecordOrderDao().load(studioId);
        if (studio != null) {
            return studio.getName();
        }
        return "Unknown Studio";
    }
}
