package com.juniperphoton.myerlistandroid.api;

import com.juniperphoton.myerlistandroid.api.response.GetOrderResponse;
import com.juniperphoton.myerlistandroid.api.response.ToDoResponse;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ToDoService {
    @FormUrlEncoded
    @POST("Schedule/GetMySchedules/v1?")
    Observable<ToDoResponse> getToDos(@Query("sid") String sid, @Field("sid") String sid2, @Query("access_token") String token);

    @FormUrlEncoded
    @POST("Schedule/GetMyOrder/v1?")
    Observable<GetOrderResponse> getOrders(@Query("sid") String sid, @Field("sid") String sid2, @Query("access_token") String token);

    @FormUrlEncoded
    @POST("Schedule/SetMyOrder/v1?")
    Observable<GetOrderResponse> setOrders(@Query("sid") String sid, @Field("sid") String sid2, @Field("order") String order, @Query("access_token") String token);
}
