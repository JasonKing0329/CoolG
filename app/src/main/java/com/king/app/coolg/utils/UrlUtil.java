package com.king.app.coolg.utils;

import com.king.app.coolg.model.http.BaseHttpClient;
import com.king.app.coolg.model.http.bean.response.PathResponse;

import io.reactivex.Observable;

public class UrlUtil {

    public static String toVideoUrl(String subPath) {
        String baseUrl = BaseHttpClient.formatUrl(BaseHttpClient.getBaseUrl());
        return baseUrl + subPath.replaceAll(" ", "%20");
    }

    public static Observable<String> toVideoUrl(PathResponse response) {
        return Observable.create(e -> {
            if (response.isAvailable()) {
                String baseUrl = BaseHttpClient.formatUrl(BaseHttpClient.getBaseUrl());
                // url中不能包含空格，用%20来代替可以达到目的
                e.onNext(baseUrl + response.getPath().replaceAll(" ", "%20"));
            }
            else {
                e.onError(new Exception("Video source is unavailable"));
            }
        });
    }

}
