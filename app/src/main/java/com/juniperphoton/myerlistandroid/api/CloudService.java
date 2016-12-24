package com.juniperphoton.myerlistandroid.api;

import com.juniperphoton.myerlistandroid.api.response.AddToDoResponse;
import com.juniperphoton.myerlistandroid.api.response.CateResponse;
import com.juniperphoton.myerlistandroid.api.response.CheckUserResponse;
import com.juniperphoton.myerlistandroid.api.response.CommonResponse;
import com.juniperphoton.myerlistandroid.api.response.GetOrderResponse;
import com.juniperphoton.myerlistandroid.api.response.GetSaltResponse;
import com.juniperphoton.myerlistandroid.api.response.LoginResponse;
import com.juniperphoton.myerlistandroid.api.response.GetToDosResponse;
import com.juniperphoton.myerlistandroid.util.AppConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

@SuppressWarnings("UnusedDeclaration")
public class CloudService {
    public static final String BASE_URL = "http://juniperphoton.net/schedule/";
    private static final int DEFAULT_TIMEOUT = 10;

    private Retrofit mRetrofit;

    public CloudService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    private static class SingletonHolder {
        private static final CloudService INSTANCE = new CloudService();
    }

    public static CloudService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Observable<CheckUserResponse> checkUserExist(String email) {
        return mRetrofit.create(UserService.class).checkUserExist(email);
    }

    public Observable<GetSaltResponse> getSalt(String email) {
        return mRetrofit.create(UserService.class).getSalt(email);
    }

    public Observable<LoginResponse> login(String email, String pwd) {
        return mRetrofit.create(UserService.class).login(email, pwd);
    }

    public Observable<CateResponse> getCategories() {
        return mRetrofit.create(CategoryService.class).getCategory(AppConfig.getSid(), AppConfig.getAccessToken());
    }

    public Observable<GetToDosResponse> getToDos() {
        return mRetrofit.create(ToDoService.class).getToDos(AppConfig.getSid(), AppConfig.getAccessToken(), AppConfig.getSid());
    }

    public Observable<GetOrderResponse> getOrders() {
        return mRetrofit.create(ToDoService.class).getOrders(AppConfig.getSid(), AppConfig.getAccessToken(), AppConfig.getSid());
    }

    public Observable<CommonResponse> setOrders(String order) {
        return mRetrofit.create(ToDoService.class).setOrders(AppConfig.getSid(), AppConfig.getAccessToken(), AppConfig.getSid(), order);
    }

    public Observable<CommonResponse> setIsDone(String id, String isDone) {
        return mRetrofit.create(ToDoService.class).setDone(AppConfig.getSid(), AppConfig.getAccessToken(), id, isDone);
    }

    public Observable<CommonResponse> deleteToDo(String id) {
        return mRetrofit.create(ToDoService.class).deleteToDo(AppConfig.getSid(), AppConfig.getAccessToken(), id);
    }

    public Observable<AddToDoResponse> addToDo(String time, String content, String isDone, String cate) {
        return mRetrofit.create(ToDoService.class).addToDo(AppConfig.getSid(), AppConfig.getAccessToken(),
                AppConfig.getSid(), time, content, isDone, cate);
    }

    public Observable<CommonResponse> updateToDo(String id, String time, String content, String cate) {
        return mRetrofit.create(ToDoService.class).updateToDo(AppConfig.getSid(), AppConfig.getAccessToken(),
                id, time, content, cate);
    }
}
