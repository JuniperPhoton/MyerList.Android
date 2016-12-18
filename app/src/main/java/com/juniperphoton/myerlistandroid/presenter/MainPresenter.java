package com.juniperphoton.myerlistandroid.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.juniperphoton.myerlistandroid.api.APIException;
import com.juniperphoton.myerlistandroid.api.CloudService;
import com.juniperphoton.myerlistandroid.api.response.CateResponse;
import com.juniperphoton.myerlistandroid.api.response.CommonResponse;
import com.juniperphoton.myerlistandroid.api.response.GetOrderResponse;
import com.juniperphoton.myerlistandroid.api.response.ToDoResponse;
import com.juniperphoton.myerlistandroid.model.CategoryInformation;
import com.juniperphoton.myerlistandroid.model.OrderedToDoList;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.util.ToastService;
import com.juniperphoton.myerlistandroid.view.MainView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter {

    private static final String TAG = "MainPresenter";

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
                .observeOn(Schedulers.io())
                .flatMap(new Func1<ToDoResponse, Observable<GetOrderResponse>>() {
                    @Override
                    public Observable<GetOrderResponse> call(final ToDoResponse toDoResponse) {
                        if (toDoResponse.getToDos() != null) {
                            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (ToDo toDo : toDoResponse.getToDos()) {
                                        realm.copyToRealmOrUpdate(toDo);
                                    }
                                }
                            });
                            Log.d(TAG, "got todos");
                            return CloudService.getInstance().getOrders();
                        } else {
                            return Observable.error(new APIException(toDoResponse.getFriendErrorMessage()));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetOrderResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetOrderResponse getOrderResponse) {
                        Log.d(TAG, "got order");
                        orderToDos(getOrderResponse);
                    }
                });
    }

    public void updateOrders(String order) {
        CloudService.getInstance().setOrders(order).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonResponse>() {
                    @Override
                    public void call(CommonResponse commonResponse) {
                        Log.d(TAG, "updateOrders");
                    }
                });
    }

    public void updateIsDone(ToDo toDo) {
        CloudService.getInstance().setIsDone(toDo.getId(), toDo.getIsdone()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonResponse>() {
                    @Override
                    public void call(CommonResponse commonResponse) {
                        Log.d(TAG, "updateIsDone");
                    }
                });
    }

    public void deleteToDo(ToDo toDo) {
        String id = toDo.getId();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        OrderedToDoList query = realm.where(OrderedToDoList.class).findFirst();
        if (query == null) return;
        RealmList<ToDo> list = query.getToDos();
        boolean ok = list.remove(toDo);
        toDo.deleteFromRealm();
        Log.d(TAG, "delete from realm:" + String.valueOf(ok));

        realm.commitTransaction();

        CloudService.getInstance().deleteToDo(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonResponse>() {
                    @Override
                    public void call(CommonResponse commonResponse) {
                        Log.d(TAG, "deleteToDo");
                    }
                });
    }

    private void orderToDos(GetOrderResponse getOrderResponse) {
        String order = getOrderResponse.getOrder();
        if (order != null) {
            final String[] orders = order.split(",");

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            final RealmList<ToDo> orderedToDos = new RealmList<>();
            RealmResults<ToDo> realmResult = realm.where(ToDo.class).findAll();
            ArrayList<ToDo> noOrderList = new ArrayList<>();
            for (ToDo toDo : realmResult) {
                noOrderList.add(toDo);
            }
            for (String id : orders) {
                ToDo toDo = realmResult.where().equalTo("id", id).findFirst();
                if (toDo != null) {
                    orderedToDos.add(toDo);
                    noOrderList.remove(toDo);
                }
            }
            for (ToDo toDo : noOrderList) {
                orderedToDos.add(toDo);
            }
            OrderedToDoList list = new OrderedToDoList();
            list.setToDos(orderedToDos);
            realm.copyToRealmOrUpdate(list);

            realm.commitTransaction();
            ToastService.sendShortToast("Fetched:D");
            mMainView.displayToDos();
        } else {
            ToastService.sendShortToast("Can't get order");
        }
    }

    private void parseCategories(String resp) {
        Gson gson = new Gson();
        final CategoryInformation information = gson.fromJson(resp, CategoryInformation.class);
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
