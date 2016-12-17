package com.juniperphoton.myerlistandroid.api;

import com.juniperphoton.myerlistandroid.api.response.ToDoResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ToDoService {
    @FormUrlEncoded
    @POST("Schedule/GetMySchedules/v1?")
    Observable<ToDoResponse> getToDos(@Query("sid") String sid, @Field("sid") String sid2, @Query("access_token") String token);
}
