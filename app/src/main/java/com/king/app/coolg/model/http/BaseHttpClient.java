package com.king.app.coolg.model.http;

import android.text.TextUtils;

import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DebugLog;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 描述: 基于retrofit2.0的http client
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/25 14:15
 */
public abstract class BaseHttpClient {

    private final boolean isDebug = true;
    private final int TIMEOUT = 15000;

    private OkHttpClient client;

    public BaseHttpClient() {
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // 打印url
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    DebugLog.e(request.url().toString());
                    return chain.proceed(request);
                })
                // 打印log
                .addInterceptor(logInterceptor);
        if (getHeaderInterceptors() != null) {
            builder.addInterceptor(getHeaderInterceptors());
        }
        if (getLogInterceptors() != null) {
            builder.addInterceptor(getLogInterceptors());
        }
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        client = builder.build();
    }

    protected abstract Interceptor getLogInterceptors();

    public void createRetrofit() throws Exception {
        createRetrofit(getBaseUrl());
    }

    public void createRetrofit(String baseUrl) throws Exception {
        String url = formatUrl(baseUrl);
        DebugLog.e(url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(getConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        createService(retrofit);
    }

    public static String formatUrl(String ip) {
        if (!ip.startsWith("http://")) {
            ip = "http://" + ip;
        }
        if (!ip.endsWith("/")) {
            ip = ip + "/";
        }
        return ip;
    }

    public static String getBaseUrl() {
        String ip = SettingProperty.getServerUrl();
        if (TextUtils.isEmpty(ip)) {
            return "http://www.baidu.com/";
        }
        return ip;
    }

    protected abstract Converter.Factory getConverterFactory();

    protected abstract Interceptor getHeaderInterceptors();

    protected abstract void createService(Retrofit retrofit);

    HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(message -> {
        if (isDebug) {
            DebugLog.e(message);
        } else {
//                ReleaseLogger.log(context, "AppHttpClient", message);
        }
    });
}
