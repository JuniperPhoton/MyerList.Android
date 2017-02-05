package com.juniperphoton.myerlist.api;

import com.juniperphoton.myerlist.api.response.CheckUserResponse;
import com.juniperphoton.myerlist.api.response.GetSaltResponse;
import com.juniperphoton.myerlist.api.response.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

@SuppressWarnings("UnusedDeclaration")
public interface UserService {
    @FormUrlEncoded
    @POST("User/CheckUserExist/v1?")
    Observable<CheckUserResponse> checkUserExist(@Field("email") String email);

    @FormUrlEncoded
    @POST("User/Register/v1?")
    Observable<ResponseBody> register(@Field("email") String email, @Field("password") String pwd);

    @FormUrlEncoded
    @POST("User/Login/v1?")
    Observable<LoginResponse> login(@Field("email") String email, @Field("password") String pwd);

    @FormUrlEncoded
    @POST("User/GetSalt/v1?")
    Observable<GetSaltResponse> getSalt(@Field("email") String email);
}
