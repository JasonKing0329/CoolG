package com.king.app.coolg.phone.star.category;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.TopStar;
import com.king.app.gdb.data.entity.TopStarCategory;
import com.king.app.gdb.data.entity.TopStarCategoryDao;
import com.king.app.gdb.data.entity.TopStarDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryDetailViewModel extends BaseViewModel {

    public MutableLiveData<List<Object>> candidatesObserver = new MutableLiveData<>();

    public MutableLiveData<List<CategoryLevel>> levelsObserver = new MutableLiveData<>();

    public MutableLiveData<Integer> insertLevelIndex = new MutableLiveData<>();

    public MutableLiveData<Integer> removeLevelIndex = new MutableLiveData<>();

    public MutableLiveData<CategoryLevel> levelDataChanged = new MutableLiveData<>();

    public MutableLiveData<CategoryStar> candidateRemoved = new MutableLiveData<>();

    public MutableLiveData<Boolean> cancelConfirmStatus = new MutableLiveData<>();

    private TopStarCategory mCategory;

    private long mCategoryId;

    private boolean mIsEditMode;

    public CategoryDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadCandidates(long categoryId) {
        mCategoryId = categoryId;
        getCategory()
                .flatMap(category -> {
                    mCategory = category;
                    return getCandidatesStar();
                })
                .flatMap(stars -> toCandidates(stars))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> objects) {
                        candidatesObserver.setValue(objects);
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

    private Observable<TopStarCategory> getCategory() {
        return Observable.create(e -> {
            TopStarCategory category = getDaoSession().getTopStarCategoryDao().queryBuilder()
                    .where(TopStarCategoryDao.Properties.Id.eq(mCategoryId))
                    .build().unique();
            e.onNext(category);
        });
    }

    private Observable<List<TopStar>> getCandidatesStar() {
        return Observable.create(e -> {
            List<TopStar> list = getDaoSession().getTopStarDao().queryBuilder()
                    .where(TopStarDao.Properties.CategoryId.eq(mCategoryId))
                    .where(TopStarDao.Properties.Level.eq(0))
                    .build().list();
            e.onNext(list);
        });
    }

    private ObservableSource<List<Object>> toCandidates(List<TopStar> stars) {
        return observer -> {
            List<Object> list = new ArrayList<>();
            // add stars
            for (TopStar star:stars) {
                CategoryStar cs = new CategoryStar();
                cs.setStar(star);
                cs.setName(star.getStar().getName());
                cs.setUrl(ImageProvider.getStarRandomPath(cs.getName(), null));
                cs.setObserver(candidateSelectObserver);
                list.add(cs);
            }
            // add 'add' icon
            CategoryAdd add = new CategoryAdd();
            list.add(add);
            observer.onNext(list);
        };
    }

    public void addCandidates(ArrayList<CharSequence> list) {
        insertCandidates(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean objects) {
                        loadCandidates(mCategoryId);
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

    private Observable<Boolean> insertCandidates(ArrayList<CharSequence> list) {
        return Observable.create(e -> {
            if (!ListUtil.isEmpty(list)) {
                List<TopStar> insertList = new ArrayList<>();
                for (CharSequence idStr:list) {
                    long starId = Long.parseLong(idStr.toString());
                    QueryBuilder<TopStar> builder = getDaoSession().getTopStarDao().queryBuilder();
                    TopStar ts = builder.where(TopStarDao.Properties.CategoryId.eq(mCategoryId))
                            .where(TopStarDao.Properties.StarId.eq(starId))
                            .build().unique();
                    if (ts == null) {
                        ts = new TopStar();
                        ts.setCategoryId(mCategoryId);
                        ts.setStarId(starId);
                        insertList.add(ts);
                    }
                }
                if (insertList.size() > 0) {
                    getDaoSession().getTopStarDao().insertInTx(insertList);
                    getDaoSession().getTopStarDao().detachAll();

                    // update star number
                    updateCategoryStarNumber(insertList.size());
                }
            }
            e.onNext(true);
        });
    }

    private void updateCategoryStarNumber(int number) {
        mCategory.setNumber(mCategory.getNumber() + number);
        getDaoSession().getTopStarCategoryDao().update(mCategory);
        getDaoSession().getTopStarCategoryDao().detach(mCategory);
    }

    public void loadLevels() {
        getLevels()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CategoryLevel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<CategoryLevel> categoryLevels) {
                        levelsObserver.setValue(categoryLevels);
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

    private CategoryStar parseStar(TopStar star) {
        CategoryStar cs = new CategoryStar();
        cs.setStar(star);
        cs.setName(star.getStar().getName());
        cs.setUrl(ImageProvider.getStarRandomPath(cs.getName(), null));
        return cs;
    }

    private Observable<List<CategoryLevel>> getLevels() {
        return Observable.create(e -> {
            List<CategoryLevel> levels = new ArrayList<>();
            List<TopStar> stars = getDaoSession().getTopStarDao().queryBuilder()
                    .where(TopStarDao.Properties.CategoryId.eq(mCategoryId))
                    .where(TopStarDao.Properties.Level.notEq(0))
                    .orderAsc(TopStarDao.Properties.Level, TopStarDao.Properties.LevelIndex)
                    .build().list();
            CategoryLevel lastLevel = null;
            for (TopStar star:stars) {
                if (lastLevel == null || star.getLevel() != lastLevel.getLevel()) {
                    lastLevel = new CategoryLevel();
                    lastLevel.setStarList(new ArrayList<>());
                    lastLevel.setLevel(star.getLevel());
                    levels.add(lastLevel);
                }
                CategoryStar cs = parseStar(star);
                lastLevel.getStarList().add(cs);
            }

            // 默认提供一个
            if (levels.size() == 0) {
                lastLevel = new CategoryLevel();
                lastLevel.setStarList(new ArrayList<>());
                lastLevel.setLevel(1);
                levels.add(lastLevel);
            }
            e.onNext(levels);
        });
    }

    private SelectObserver<CategoryStar> candidateSelectObserver = data -> onSelectCandidate(data);

    private void onSelectCandidate(CategoryStar data) {
        if (!mIsEditMode) {
            return;
        }
        selectCandidate(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TopStar>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(TopStar topStar) {

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

    public void onSelectLevelStar(CategoryLevel level, CategoryStar star) {
        if (!mIsEditMode) {
            return;
        }
        selectLevelStar(level, star)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TopStar>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(TopStar topStar) {

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

    /**
     * add to level and remove from candidates
     * @param data
     * @return
     */
    private Observable<TopStar> selectCandidate(CategoryStar data) {
        return Observable.create(e -> {
            CategoryLevel targetLevel = getSelectedLevel();
            if (targetLevel == null) {
                e.onError(new Exception("No selected level"));
            }
            else {
                // add to level
                TopStar ts = data.getStar();
                ts.setLevel(targetLevel.getLevel());
                ts.setLevelIndex(targetLevel.getLevel());
                data.setObserver(null);
                targetLevel.getStarList().add(data);
                levelDataChanged.postValue(targetLevel);
                // remove from candidates
                candidateRemoved.postValue(data);

                e.onNext(ts);
            }
        });
    }

    /**
     * add to candidates and remove from level
     *
     * @param level
     * @param star
     * @return
     */
    private Observable<TopStar> selectLevelStar(CategoryLevel level, CategoryStar star) {
        return Observable.create(e -> {
            // add to candidates
            TopStar ts = star.getStar();
            ts.setLevel(0);
            ts.setLevelIndex(0);
            star.setObserver(candidateSelectObserver);
            candidatesObserver.getValue().add(0, star);
            candidatesObserver.postValue(candidatesObserver.getValue());
            // remove from level
            level.getStarList().remove(star);
            levelDataChanged.postValue(level);

            e.onNext(ts);
        });
    }

    private CategoryLevel getSelectedLevel() {
        CategoryLevel categoryLevel = null;
        for (CategoryLevel level:levelsObserver.getValue()) {
            if (level.isSelected()) {
                categoryLevel = level;
                break;
            }
        }
        return categoryLevel;
    }

    public void addNewLevelAfter(CategoryLevel level) {
        int insertIndex = level.getLevel();
        // 修正后面的level
        for (int i = insertIndex; i < levelsObserver.getValue().size(); i ++) {
            CategoryLevel cl = levelsObserver.getValue().get(i);
            cl.setLevel(cl.getLevel() + 1);
        }
        CategoryLevel newLevel = new CategoryLevel();
        newLevel.setLevel(insertIndex + 1);
        newLevel.setStarList(new ArrayList<>());
        levelsObserver.getValue().add(insertIndex, newLevel);
        insertLevelIndex.setValue(insertIndex);
    }

    public void setEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void removeLevel(CategoryLevel level) {

        if (level.getLevel() == 1) {
            messageObserver.setValue("Level 1 is not allowed to be removed");
            return;
        }

        if (!ListUtil.isEmpty(level.getStarList())) {
            messageObserver.setValue("Please remove all stars of this level first");
            return;
        }

        int index = level.getLevel() - 1;
        // 修正后面的level
        for (int i = index + 1; i < levelsObserver.getValue().size(); i ++) {
            CategoryLevel cl = levelsObserver.getValue().get(i);
            cl.setLevel(cl.getLevel() - 1);
        }
        levelsObserver.getValue().remove(index);
        removeLevelIndex.setValue(index);
    }

    public void saveCategoryDetail() {
        loadingObserver.setValue(true);
        updateDetails()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean topStar) {
                        loadingObserver.setValue(false);
                        cancelConfirmStatus.setValue(true);
                        messageObserver.setValue("Save success");
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

    private Observable<Boolean> updateDetails() {
        return Observable.create(e -> {
            List<TopStar> updateList = new ArrayList<>();
            // level stars
            for (CategoryLevel level:levelsObserver.getValue()) {
                if (ListUtil.isEmpty(level.getStarList())) {
                    e.onError(new Exception("Level " + level.getLevel() + " is empty, please remove or fill it first"));
                    break;
                }
                for (int i = 0; i < level.getStarList().size(); i ++) {
                    CategoryStar cs = (CategoryStar) level.getStarList().get(i);
                    cs.getStar().setLevel(level.getLevel());
                    cs.getStar().setLevelIndex(i);
                    updateList.add(cs.getStar());
                }
            }
            // candidates
            for (Object object:candidatesObserver.getValue()) {
                if (object instanceof CategoryStar) {
                    CategoryStar star = (CategoryStar) object;
                    updateList.add(star.getStar());
                }
            }

            if (updateList.size() > 0) {
                getDaoSession().getTopStarDao().updateInTx(updateList);
            }
            e.onNext(true);
        });
    }

    public void deleteCandidates(CategoryStar star) {
        getDaoSession().getTopStarDao().delete(star.getStar());
        candidatesObserver.getValue().remove(star);
        // update star number
        updateCategoryStarNumber(-1);
    }

    public void cancelEdit() {
        getDaoSession().getTopStarDao().detachAll();
    }
}
