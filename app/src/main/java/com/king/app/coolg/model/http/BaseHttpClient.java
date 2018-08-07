package com.king.app.coolg.model.http;

import android.text.TextUtils;

import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DebugLog;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        client = builder.build();
        createRetrofit();
    }

    protected void createRetrofit() {
        String baseUrl = getBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl.concat("/");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(getConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        createService(retrofit);
    }

    public static String getBaseUrl() {
        String ip = SettingProperty.getServerUrl();
        if (TextUtils.isEmpty(ip)) {
            return "http://www.baidu.com/";
        }
        else {
            if (!ip.startsWith("http://")) {
                ip = "http://" + ip;
            }
            if (!ip.endsWith("/")) {
                ip = ip + "/";
            }
        }
        return ip;
    }

    protected abstract Converter.Factory getConverterFactory();

    protected abstract Interceptor getHeaderInterceptors();

    protected abstract void createService(Retrofit retrofit);

    HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(message -> {
        if (isDebug) {
            if (message != null && message.startsWith("{")) {
                try {
                    Logger.json(message);
                } catch (Exception e) {
                    Logger.d(message);
                }
            } else {
                // 不打印其他类信息
                Logger.d(message);
            }
        } else {
//                ReleaseLogger.log(context, "AppHttpClient", message);
        }
    });
}
