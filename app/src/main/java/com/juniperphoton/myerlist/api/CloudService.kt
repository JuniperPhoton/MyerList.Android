package com.juniperphoton.myerlist.api

import com.juniperphoton.myerlist.api.response.*
import com.juniperphoton.myerlist.util.AppConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.concurrent.TimeUnit

object CloudService {
    private val BASE_URL = "http://juniperphoton.net/schedule/"
    private val DEFAULT_TIMEOUT = 10

    private val retrofit: Retrofit

    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)

        retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build()
    }

    fun checkUserExist(email: String): Observable<CheckUserResponse> {
        return retrofit.create(UserService::class.java).checkUserExist(email)
    }

    fun getSalt(email: String): Observable<GetSaltResponse> {
        return retrofit.create(UserService::class.java).getSalt(email)
    }

    fun login(email: String, pwd: String): Observable<LoginResponse> {
        return retrofit.create(UserService::class.java).login(email, pwd)
    }

    fun register(email: String, pwd: String): Observable<RegisterResponse> {
        return retrofit.create(UserService::class.java).register(email, pwd)
    }

    fun getCategories(): Observable<CateResponse> = retrofit.create(CategoryService::class.java)
            .getCategory(AppConfig.sid!!, AppConfig.accessToken!!)

    fun getToDos(): Observable<GetToDosResponse> = retrofit.create(ToDoService::class.java)
            .getToDos(AppConfig.sid!!, AppConfig.accessToken!!, AppConfig.sid)

    fun getOrders(): Observable<GetOrderResponse> = retrofit.create(ToDoService::class.java)
            .getOrders(AppConfig.sid!!, AppConfig.accessToken!!, AppConfig.sid)

    fun setOrders(order: String): Observable<CommonResponse> {
        return retrofit.create(ToDoService::class.java)
                .setOrders(AppConfig.sid!!, AppConfig.accessToken!!, AppConfig.sid, order)
    }

    fun setIsDone(id: String, isDone: String): Observable<CommonResponse> {
        return retrofit.create(ToDoService::class.java)
                .setDone(AppConfig.sid!!, AppConfig.accessToken!!, id, isDone)
    }

    fun deleteToDo(id: String): Observable<CommonResponse> {
        return retrofit.create(ToDoService::class.java).deleteToDo(AppConfig.sid!!, AppConfig.accessToken!!, id)
    }

    fun addToDo(time: String, content: String, isDone: String, cate: String): Observable<AddToDoResponse> {
        return retrofit.create(ToDoService::class.java).addToDo(AppConfig.sid!!, AppConfig.accessToken!!,
                AppConfig.sid, time, content, isDone, cate)
    }

    fun updateToDo(id: String, time: String, content: String, cate: String): Observable<CommonResponse> {
        return retrofit.create(ToDoService::class.java).updateToDo(AppConfig.sid!!, AppConfig.accessToken!!,
                id, time, content, cate)
    }

    fun updateToDoCategories(str: String): Observable<CommonResponse> {
        return retrofit.create(CategoryService::class.java).setCategory(AppConfig.sid!!, AppConfig.accessToken!!,
                str, AppConfig.sid, AppConfig.accessToken)
    }
}