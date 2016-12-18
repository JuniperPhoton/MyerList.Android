package com.juniperphoton.myerlistandroid.api;

import com.juniperphoton.myerlistandroid.api.response.CommonResponse;
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
    Observable<ToDoResponse> getToDos(@Query("sid") String sid, @Query("access_token") String token,
                                      @Field("sid") String sid2);

    @FormUrlEncoded
    @POST("Schedule/GetMyOrder/v1?")
    Observable<GetOrderResponse> getOrders(@Query("sid") String sid, @Query("access_token") String token,
                                           @Field("sid") String sid2);

    @FormUrlEncoded
    @POST("Schedule/SetMyOrder/v1?")
    Observable<CommonResponse> setOrders(@Query("sid") String sid, @Query("access_token") String token,
                                         @Field("sid") String sid2, @Field("order") String order);

    @FormUrlEncoded
    @POST("Schedule/FinishSchedule/v1?")
    Observable<CommonResponse> setDone(@Query("sid") String sid, @Query("access_token") String token,
                                       @Field("id") String id, @Field("isdone") String isDone);

    @FormUrlEncoded
    @POST("Schedule/DeleteSchedule/v1?")
    Observable<CommonResponse> deleteToDo(@Query("sid") String sid, @Query("access_token") String token,
                                          @Field("id") String id);
}
