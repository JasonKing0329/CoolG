package com.king.app.coolg.phone.record.scene;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.ColorUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/13 9:59
 */
public class SceneViewModel extends BaseViewModel {

    private RecordRepository repository;

    public MutableLiveData<List<SceneBean>> sceneObserver = new MutableLiveData<>();

    private int mSortType;

    private int mRecordType;

    private List<SceneBean> mSceneList;

    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        mSortType = AppConstants.SCENE_SORT_NAME;
    }

    public void setRecordType(int mRecordType) {
        this.mRecordType = mRecordType;
    }

    public void loadScenes() {
        repository.getScenes(mRecordType)
                .flatMap(list -> {
                    // add 'All'
                    SceneBean bean = new SceneBean();
                    bean.setScene(AppConstants.KEY_SCENE_ALL);
                    bean.setNumber((int) repository.getRecordCount(0));
                    list.add(0, bean);
                    return loadColors(list, SettingProperty.getSceneHsvColor());
                })
                .flatMap(list -> sortScenes(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SceneBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SceneBean> list) {
                        mSceneList = list;
                        sceneObserver.setValue(list);
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

    private Observable<List<SceneBean>> loadColors(List<SceneBean> list, HsvColorBean colorBean) {
        return Observable.create(e -> {
            int defaultColor = getApplication().getResources().getColor(R.color.colorPrimary);
            for (SceneBean bean:list) {
                if (colorBean == null) {
                    bean.setColor(defaultColor);
                }
                else {
                    bean.setColor(ColorUtil.randomColorBy(colorBean));
                }
            }
            e.onNext(list);
        });
    }

    private Observable<List<SceneBean>> sortScenes(List<SceneBean> list) {
        return Observable.create(e -> {
            List<SceneBean> sceneList = list.subList(1, list.size() - 1);
            switch (mSortType) {
                case AppConstants.SCENE_SORT_NAME:
                    Collections.sort(sceneList, new SceneNameComparator());
                    break;
                case AppConstants.SCENE_SORT_NUMBER:
                    Collections.sort(sceneList, new SceneNumberComparator());
                    break;
                case AppConstants.SCENE_SORT_AVG:
                    Collections.sort(sceneList, new SceneAverageComparator());
                    break;
                case AppConstants.SCENE_SORT_MAX:
                    Collections.sort(sceneList, new SceneMaxComparator());
                    break;
            }
            sceneList.add(0, list.get(0));
            e.onNext(sceneList);
        });
    }

    /**
     * sort by sort type
     * @param mSortType
     */
    public void sort(int mSortType) {
        this.mSortType = mSortType;
        if (mSceneList == null) {
            return;
        }
        sortScenes(mSceneList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SceneBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SceneBean> list) {
                        mSceneList = list;
                        sceneObserver.setValue(list);
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

    public void updateTempColor(HsvColorBean hsvColorBean) {
        loadColors(mSceneList, hsvColorBean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SceneBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SceneBean> list) {
                        mSceneList = list;
                        sceneObserver.setValue(list);
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

    public class SceneNameComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            return l.getScene().toLowerCase().compareTo(r.getScene().toLowerCase());
        }
    }

    public class SceneAverageComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getAverage() - l.getAverage() > 0) {
                return 1;
            }
            else if (r.getAverage() - l.getAverage() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    public class SceneNumberComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getNumber() - l.getNumber() > 0) {
                return 1;
            }
            else if (r.getNumber() - l.getNumber() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    public class SceneMaxComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getMax() - l.getMax() > 0) {
                return 1;
            }
            else if (r.getMax() - l.getMax() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

}
