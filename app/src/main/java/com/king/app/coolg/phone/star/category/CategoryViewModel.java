package com.king.app.coolg.phone.star.category;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.gdb.data.entity.TopStarCategory;
import com.king.app.gdb.data.entity.TopStarCategoryDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryViewModel extends BaseViewModel {

    public MutableLiveData<List<CategoryViewItem>> categoryObserver = new MutableLiveData<>();

    public MutableLiveData<TopStarCategory> existedCategoryObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> hideConfirmStatus = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadCategories() {
        getCategories()
                .flatMap(list -> toViewItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CategoryViewItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<CategoryViewItem> topStarCategories) {
                        categoryObserver.setValue(topStarCategories);
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

    private Observable<List<TopStarCategory>> getCategories() {
        return Observable.create(e -> e.onNext(getDaoSession().getTopStarCategoryDao().loadAll()));
    }

    private ObservableSource<List<CategoryViewItem>> toViewItems(List<TopStarCategory> list) {
        return observer -> {
            List<CategoryViewItem> items = new ArrayList<>();
            for (TopStarCategory category:list) {
                CategoryViewItem item = new CategoryViewItem();
                item.setCategory(category);
                item.setVisibility(View.GONE);
                item.setObserver(selectObserver);
                items.add(item);
            }
            observer.onNext(items);
        };
    }

    private SelectObserver<CategoryViewItem> selectObserver = data -> onSelectCategory(data);

    private void onSelectCategory(CategoryViewItem data) {
        data.setSelected(!data.isSelected());
    }

    private class InsertObserver implements Observer<TopStarCategory> {
        @Override
        public void onSubscribe(Disposable d) {
            addDisposable(d);
        }

        @Override
        public void onNext(TopStarCategory category) {
            loadCategories();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {

        }
    }

    public void forceInsertCategory(TopStarCategory category) {
        insertCategory(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new InsertObserver());
    }

    private Observable<TopStarCategory> insertCategory(TopStarCategory category) {
        return Observable.create(e -> {
            getDaoSession().getTopStarCategoryDao().insert(category);
            e.onNext(category);
        });
    }

    public void addCategory(String name, int type) {
        TopStarCategory category = new TopStarCategory();
        category.setName(name);
        category.setType(type);
        checkExist(category)
                .flatMap(exist -> {
                    if (exist) {
                        return (ObservableSource<TopStarCategory>) observer -> {
                            existedCategoryObserver.setValue(category);
                            observer.onError(new Exception("Category Existed"));
                        };
                    }
                    else {
                        return insertCategory(category);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new InsertObserver());
    }

    private Observable<Boolean> checkExist(TopStarCategory category) {
        return Observable.create(e -> {
            List<TopStarCategory> ts = getDaoSession().getTopStarCategoryDao().queryBuilder()
                    .where(TopStarCategoryDao.Properties.Name.eq(category.getName()))
                    .build().list();
            e.onNext(ts.size() > 0);
        });
    }

    public void deleteSelected() {
        deleteItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        messageObserver.setValue("Delete success");
                        hideConfirmStatus.setValue(true);
                        loadCategories();
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

    private Observable<Boolean> deleteItems() {
        return Observable.create(e -> {
            List<TopStarCategory> deleteList = new ArrayList<>();
            for (CategoryViewItem item:categoryObserver.getValue()) {
                if (item.isSelected()) {
                    deleteList.add(item.getCategory());
                }
            }
            if (deleteList.size() > 0) {
                getDaoSession().getTopStarCategoryDao().deleteInTx(deleteList);
            }
            e.onNext(true);
        });
    }
}
