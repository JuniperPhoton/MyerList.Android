package com.juniperphoton.myerlistandroid.api;


import com.juniperphoton.myerlistandroid.api.response.GetSaltResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("UnusedDeclaration")
public interface CategoryService {
    @FormUrlEncoded
    @POST("User/GetCateInfo/v1?")
    Observable<GetSaltResponse> getCategory(@Query("sid") String sid, @Query("access_token") String token);
}
