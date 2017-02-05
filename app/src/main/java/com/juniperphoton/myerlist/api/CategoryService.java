package com.juniperphoton.myerlist.api;

import com.juniperphoton.myerlist.api.response.CateResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("UnusedDeclaration")
public interface CategoryService {
    @GET("User/GetCateInfo/v1?")
    Observable<CateResponse> getCategory(@Query("sid") String sid, @Query("access_token") String token);
}
