package com.king.app.coolg.model.http.bean.response;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/12 0012 14:00
 */

public class BaseFlatMap {

    public static <T> ObservableSource<T> result(final BaseResponse<T> response) {
        return new ObservableSource<T>() {
            @Override
            public void subscribe(Observer<? super T> observer) {
                if (response.getResult() == 1) {
                    observer.onNext(response.getData());
                }
                else {
                    observer.onError(new Throwable(response.getMessage()));
                }
            }
        };
    }

    public static <T> ObservableSource resultIncludeNull(final BaseResponse<T> response) {
        return new ObservableSource() {
            @Override
            public void subscribe(Observer observer) {
                if (response.getResult() == 1) {
                    if (response.getData() == null) {
                        observer.onNext(new Object());
                    }
                    else {
                        observer.onNext(response.getData());
                    }
                }
                else {
                    observer.onError(new Throwable(response.getMessage()));
                }
            }
        };
    }
}
