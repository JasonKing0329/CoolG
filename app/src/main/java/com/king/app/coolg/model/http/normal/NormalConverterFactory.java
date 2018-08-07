package com.king.app.coolg.model.http.normal;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 描述: 普通网络请求converter
 * 仿照GsonConverterFactory写的，用于记录日志
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/25 11:20
 */
public class NormalConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static NormalConverterFactory create() {
        return new NormalConverterFactory();
    }

    private NormalConverterFactory() {
        gson = new Gson();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new NormalRequestConverter<>(gson, adapter);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new NormalResponseConverter<>(gson, adapter);
    }
}
