package com.king.app.coolg.model.http;

import android.text.TextUtils;

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

    /**
     * @param url
     * @param port
     * @param secondUrl
     * @throws EmptyUrlException
     */
    public static String parseUrl(String url, String port, String secondUrl) throws EmptyUrlException {
        String baseUrl = url;
        if (TextUtils.isEmpty(url)) {
            throw new EmptyUrlException();
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            baseUrl = "http://".concat(url);
        }
        if (!TextUtils.isEmpty(port)) {
            baseUrl = baseUrl.concat(":").concat(port);
        }
        if (!TextUtils.isEmpty(secondUrl)) {
            if (!secondUrl.startsWith("/")) {
                baseUrl = baseUrl.concat("/");
            }
            baseUrl = baseUrl.concat(secondUrl);
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl.concat("/");
        }
        DebugLog.e(baseUrl);
        return baseUrl;
    }
    public abstract String getBaseUrl();

    public BaseHttpClient() {
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // 打印url
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
//                        CrmLogger.crmlog(BuffetApplication.getInstance().getApplicationContext(), "Request", request.url().toString());
                        return chain.proceed(request);
                    }
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

    protected abstract Converter.Factory getConverterFactory();

    protected abstract Interceptor getHeaderInterceptors();

    protected abstract void createService(Retrofit retrofit);

    /**
     * 已经初始化过baseUrl，改变baseUrl需要重新实例化baseUrl
     *
     * @param url
     * @throws EmptyUrlException
     */
    public abstract void onBaseUrlChanged(String url) throws EmptyUrlException;

    /**
     * 已经初始化过baseUrl，改变baseUrl需要重新实例化baseUrl
     *
     * @param url
     * @param port
     * @param secondUrl
     * @throws EmptyUrlException
     */
    public abstract void onBaseUrlChanged(String url, String port, String secondUrl) throws EmptyUrlException;

    HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
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
        }
    });
}
