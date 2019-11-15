package com.king.app.coolg.model.http.upload;

import com.king.app.coolg.model.http.bean.response.BaseResponse;
import com.king.app.coolg.model.http.bean.response.UploadResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface UploadRespGsonService {

    @Multipart
    @POST("uploaddb")
    Observable<BaseResponse<UploadResponse>> uploadDb(@PartMap Map<String, RequestBody> params);
}
