package com.king.app.coolg.model.http.normal;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 描述: 普通网络请求响应拦截
 * 仿照GsonResponseBodyConverter写的，用于记录日志
 * <p>
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/25 11:19
 */
public class NormalResponseConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public NormalResponseConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            T result = adapter.read(jsonReader);
//            CrmLogger.crmlog(BuffetApplication.getInstance().getApplicationContext(), "Response", adapter.toJson(result));
            return result;
        } finally {
            value.close();
        }
    }
}
