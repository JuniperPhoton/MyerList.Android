package com.juniperphoton.myerlist.api

import com.juniperphoton.myerlist.api.response.CateResponse
import com.juniperphoton.myerlist.api.response.CommonResponse

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface CategoryService {
    @GET("User/GetCateInfo/v1?")
    fun getCategory(@Query("sid") sid: String, @Query("access_token") token: String): Observable<CateResponse>

    @POST("User/UpdateCateInfo/v1?")
    @FormUrlEncoded
    fun setCategory(@Query("sid") sid: String, @Query("access_token") token: String,
                    @Field("cate_info") str: String, @Field("sid") sid2: String,
                    @Field("access_token") token2: String): Observable<CommonResponse>
}