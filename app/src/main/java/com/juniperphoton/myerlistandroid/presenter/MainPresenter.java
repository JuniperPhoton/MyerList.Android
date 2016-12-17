package com.juniperphoton.myerlistandroid.presenter;

import com.google.gson.Gson;
import com.juniperphoton.myerlistandroid.api.APIException;
import com.juniperphoton.myerlistandroid.api.CloudService;
import com.juniperphoton.myerlistandroid.api.response.CateResponse;
import com.juniperphoton.myerlistandroid.api.response.ToDoResponse;
import com.juniperphoton.myerlistandroid.model.CategoryInfomation;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.util.ToastService;
import com.juniperphoton.myerlistandroid.view.MainView;

import io.realm.Realm;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter {

    private MainView mMainView;

    public MainPresenter(MainView mainView) {
        mMainView = mainView;
    }

    public void getCate() {
        CloudService.getInstance().getCates()
                .subscribeOn(Schedulers.io())
                .map(new Func1<CateResponse, Object>() {
                    @Override
                    public Object call(CateResponse cateResponse) {
                        parseCategories(cateResponse.getRawString());
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object cateResponse) {
                        mMainView.displayCategories();
                        getToDos();
                    }
                });
    }

    public void getToDos() {
        CloudService.getInstance().getToDos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ToDoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ToDoResponse toDoResponse) {
                        if (toDoResponse.getToDos() != null) {
                            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (ToDo toDo : toDoResponse.getToDos()) {
                                        realm.copyToRealmOrUpdate(toDo);
                                    }
                                }
                            });
                            ToastService.sendShortToast("Fetched:D");
                            mMainView.displayToDos();
                        } else {
                            ToastService.sendShortToast(toDoResponse.getFriendErrorMessage());
                        }
                    }
                });
    }

    private void parseCategories(String resp) {
        Gson gson = new Gson();
        final CategoryInfomation information = gson.fromJson(resp, CategoryInfomation.class);
        if (information.isModified()) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (ToDoCategory cate : information.getCates()) {
                        realm.copyToRealmOrUpdate(cate);
                    }
                }
            });
        } else {

        }
    }
}
