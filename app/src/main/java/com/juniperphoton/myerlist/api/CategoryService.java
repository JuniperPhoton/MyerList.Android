package com.juniperphoton.myerlist.api;

import com.juniperphoton.myerlist.api.response.CateResponse;
import com.juniperphoton.myerlist.api.response.CommonResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("UnusedDeclaration")
public interface CategoryService {
    @GET("User/GetCateInfo/v1?")
    Observable<CateResponse> getCategory(@Query("sid") String sid, @Query("access_token") String token);

    @POST("User/UpdateCateInfo/v1?")
    @FormUrlEncoded
    Observable<CommonResponse> setCategory(@Query("sid") String sid, @Query("access_token") String token,
                                           @Field("cate_info") String str, @Field("sid") String sid2,
                                           @Field("access_token") String token2);
}
