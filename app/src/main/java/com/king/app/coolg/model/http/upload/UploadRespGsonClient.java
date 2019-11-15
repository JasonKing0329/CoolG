package com.king.app.coolg.model.http.upload;

import com.king.app.coolg.model.http.BaseHttpClient;

import okhttp3.Interceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 描述: upload
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/6 14:52
 */
public class UploadRespGsonClient extends BaseHttpClient {

    private UploadRespGsonService service;
    private volatile static UploadRespGsonClient instance;
    private UploadLogInterceptor logInterceptor;

    private UploadRespGsonClient() {
        super();
        try {
            createRetrofit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Converter.Factory getConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Override
    protected Interceptor getHeaderInterceptors() {
        return null;
    }

    @Override
    protected Interceptor getLogInterceptors() {
        if (logInterceptor == null) {
            logInterceptor = new UploadLogInterceptor();
        }
        return logInterceptor;
    }

    @Override
    protected void createService(Retrofit retrofit) {
        service = retrofit.create(UploadRespGsonService.class);
    }

    public UploadRespGsonService getService() {
        return service;
    }

    public static UploadRespGsonClient getInstance() {
        if (instance == null) {
            synchronized (UploadRespGsonClient.class) {
                if (instance == null) {
                    instance = new UploadRespGsonClient();
                }
            }
        }
        return instance;
    }

}
