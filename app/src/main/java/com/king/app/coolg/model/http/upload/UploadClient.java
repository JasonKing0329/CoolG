package com.king.app.coolg.model.http.upload;

import com.king.app.coolg.model.http.BaseHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 文件上传
 * 不指定converter factory，只实现文件上传，service的Observable用<ResponseBody>
 */
public class UploadClient {

    private UploadService uploadService;

    // UploadClient每次都是new，直接获取最新的baseUrl即可
    public UploadClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseHttpClient.formatUrl(BaseHttpClient.getBaseUrl()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        uploadService = retrofit.create(UploadService.class);
    }

    public UploadService getUploadService() {
        return uploadService;
    }
}
