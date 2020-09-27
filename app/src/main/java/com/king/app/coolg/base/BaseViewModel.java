package com.king.app.coolg.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.gdb.data.entity.DaoSession;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/2 15:02
 */
public class BaseViewModel extends AndroidViewModel {

    protected CompositeDisposable compositeDisposable;

    public MutableLiveData<Boolean> loadingObserver = new MutableLiveData<>();
    public MutableLiveData<String> messageObserver = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected void dispatchCommonError(Throwable e) {
        messageObserver.setValue("Load error: " + e.getMessage());
    }

    protected void dispatchCommonError(String errorTitle, Throwable e) {
        messageObserver.setValue(errorTitle + ": " + e.getMessage());
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }

    protected DaoSession getDaoSession() {
        return CoolApplication.getInstance().getDaoSession();
    }

    @SuppressWarnings("unchecked")
    public <T> ObservableTransformer<T, T> applySchedulers() {
        return (ObservableTransformer<T, T>) transformer;
    }

    private final static ObservableTransformer transformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

}
