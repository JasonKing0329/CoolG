package com.king.app.coolg.model.http.normal;


import com.king.app.coolg.model.http.bean.response.BaseResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * <p/>作者： lxg
 * <p/>创建时间: 18/01/04 11:21.
 */

public class BaseResponseFlatMap {

    public static <T> Observable<T> result(final BaseResponse<T> baseRespone) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                if (baseRespone.getResult() == 1) {
                    if (baseRespone.getData() == null) {
                        e.onError(new Exception("data is null!"));
                    } else {
                        e.onNext(baseRespone.getData());
                        e.onComplete();
                    }
                } else {
                    e.onError(new Throwable(baseRespone.getMessage()));
                }
            }
        });
    }

    /**
     * 埋单接口、撤销埋单接口
     * 只看result，data始终为null
     * @param baseRespone
     * @return
     */
    public static Observable<String> resultNullData(final BaseResponse<String> baseRespone) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (baseRespone.getResult() == 1) {
                    e.onNext(baseRespone.getMessage() == null ? "":baseRespone.getMessage());
                    e.onComplete();
                } else {
                    e.onError(new Throwable(baseRespone.getMessage()));
                }
            }
        });
    }
}
