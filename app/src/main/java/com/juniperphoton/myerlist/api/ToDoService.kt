package com.juniperphoton.myerlist.api

import com.juniperphoton.myerlist.api.response.AddToDoResponse
import com.juniperphoton.myerlist.api.response.CommonResponse
import com.juniperphoton.myerlist.api.response.GetOrderResponse
import com.juniperphoton.myerlist.api.response.GetToDosResponse

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface ToDoService {
    @FormUrlEncoded
    @POST("Schedule/GetMySchedules/v1?")
    fun getToDos(@Query("sid") sid: String, @Query("access_token") token: String,
                 @Field("sid") sid2: String): Observable<GetToDosResponse>

    @FormUrlEncoded
    @POST("Schedule/GetMyOrder/v1?")
    fun getOrders(@Query("sid") sid: String, @Query("access_token") token: String,
                  @Field("sid") sid2: String): Observable<GetOrderResponse>

    @FormUrlEncoded
    @POST("Schedule/SetMyOrder/v1?")
    fun setOrders(@Query("sid") sid: String, @Query("access_token") token: String,
                  @Field("sid") sid2: String, @Field("order") order: String): Observable<CommonResponse>

    @FormUrlEncoded
    @POST("Schedule/FinishSchedule/v1?")
    fun setDone(@Query("sid") sid: String, @Query("access_token") token: String,
                @Field("id") id: String, @Field("isdone") isDone: String): Observable<CommonResponse>

    @FormUrlEncoded
    @POST("Schedule/DeleteSchedule/v1?")
    fun deleteToDo(@Query("sid") sid: String, @Query("access_token") token: String,
                   @Field("id") id: String): Observable<CommonResponse>

    @FormUrlEncoded
    @POST("Schedule/AddSchedule/v1?")
    fun addToDo(@Query("sid") sid: String, @Query("access_token") token: String,
                @Field("sid") sid2: String, @Field("time") time: String,
                @Field("content") content: String, @Field("isdone") isDone: String,
                @Field("cate") cate: String): Observable<AddToDoResponse>

    @FormUrlEncoded
    @POST("Schedule/UpdateContent/v1?")
    fun updateToDo(@Query("sid") sid: String, @Query("access_token") token: String,
                   @Field("id") id: String, @Field("time") time: String,
                   @Field("content") content: String, @Field("cate") cate: String): Observable<CommonResponse>
}