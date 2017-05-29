package com.juniperphoton.myerlist.api

import com.juniperphoton.myerlist.api.response.CheckUserResponse
import com.juniperphoton.myerlist.api.response.GetSaltResponse
import com.juniperphoton.myerlist.api.response.LoginResponse
import com.juniperphoton.myerlist.api.response.RegisterResponse

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface UserService {
    @FormUrlEncoded
    @POST("User/CheckUserExist/v1?")
    fun checkUserExist(@Field("email") email: String): Observable<CheckUserResponse>

    @FormUrlEncoded
    @POST("User/Register/v1?")
    fun register(@Field("email") email: String, @Field("password") pwd: String): Observable<RegisterResponse>

    @FormUrlEncoded
    @POST("User/Login/v1?")
    fun login(@Field("email") email: String, @Field("password") pwd: String): Observable<LoginResponse>

    @FormUrlEncoded
    @POST("User/GetSalt/v1?")
    fun getSalt(@Field("email") email: String): Observable<GetSaltResponse>
}