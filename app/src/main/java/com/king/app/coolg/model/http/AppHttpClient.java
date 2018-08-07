package com.king.app.coolg.model.http;

import com.king.app.coolg.model.http.normal.NormalConverterFactory;

import okhttp3.Interceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 描述: 通用业务，无加密
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/6 14:52
 */
public class AppHttpClient extends BaseHttpClient {

    private AppService appService;
    private volatile static AppHttpClient instance;

    private AppHttpClient() {
        super();
    }

    @Override
    protected Converter.Factory getConverterFactory() {
        return NormalConverterFactory.create();
    }

    @Override
    protected Interceptor getHeaderInterceptors() {
        return null;
    }

    @Override
    protected void createService(Retrofit retrofit) {
        appService = retrofit.create(AppService.class);
    }

    public AppService getAppService() {
        return appService;
    }

    public static AppHttpClient getInstance() {
        if (instance == null) {
            synchronized (AppHttpClient.class) {
                if (instance == null) {
                    instance = new AppHttpClient();
                }
            }
        }
        return instance;
    }

}
