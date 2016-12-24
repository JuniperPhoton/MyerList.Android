package com.juniperphoton.myerlistandroid.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.juniperphoton.myerlistandroid.api.APIException;
import com.juniperphoton.myerlistandroid.api.CloudService;
import com.juniperphoton.myerlistandroid.api.response.AddToDoResponse;
import com.juniperphoton.myerlistandroid.api.response.CateResponse;
import com.juniperphoton.myerlistandroid.api.response.CommonResponse;
import com.juniperphoton.myerlistandroid.api.response.GetOrderResponse;
import com.juniperphoton.myerlistandroid.api.response.GetToDosResponse;
import com.juniperphoton.myerlistandroid.model.CategoryRespInformation;
import com.juniperphoton.myerlistandroid.model.DeletedList;
import com.juniperphoton.myerlistandroid.model.OrderedCateList;
import com.juniperphoton.myerlistandroid.model.OrderedToDoList;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.util.AppConfig;
import com.juniperphoton.myerlistandroid.util.ToastService;
import com.juniperphoton.myerlistandroid.view.MainView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements Presenter {

    private static final String TAG = "MainPresenter";

    private MainView mMainView;

    public MainPresenter(MainView mainView) {
        mMainView = mainView;
    }

    public void getCate() {
        CloudService.getInstance().getCategories()
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
                .flatMap(new Func1<GetToDosResponse, Observable<GetOrderResponse>>() {
                    @Override
                    public Observable<GetOrderResponse> call(final GetToDosResponse toDoResponse) {
                        if (toDoResponse.getToDos() != null) {
                            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(ToDo.class);
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

    public void modifyToDo(final String cate, final String content, final String id) {
        final String dateStr = getDateStr();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ToDo toDo = realm.where(ToDo.class).equalTo("id", id).findFirst();
        if (toDo != null) {
            toDo.setContent(content);
            toDo.setCate(cate);
            toDo.setTime(dateStr);
        }
        realm.commitTransaction();
        CloudService.getInstance().updateToDo(id, dateStr, content, cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommonResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommonResponse commonResponse) {
                        if (commonResponse.getOK()) {
                            ToastService.sendShortToast("Modified.");
                        } else {
                            ToastService.sendShortToast(commonResponse.getFriendErrorMessage());
                            Log.e(TAG, "Modify failed:" + commonResponse.getFriendErrorMessage());
                        }
                    }
                });
    }

    private String getDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    public void addToDo(String cate, String content) {
        String dateStr = getDateStr();
        CloudService.getInstance().addToDo(dateStr, content, "0", cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddToDoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastService.sendShortToast(e.getMessage());
                    }

                    @Override
                    public void onNext(AddToDoResponse addToDoResponse) {
                        ToDo toDo = addToDoResponse.getToDo();
                        if (toDo != null) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            OrderedToDoList orderList = realm.where(OrderedToDoList.class).findFirst();
                            if (orderList == null) {
                                orderList = new OrderedToDoList();
                                orderList = realm.copyFromRealm(orderList);
                            }
                            ToDo managedToDo = realm.copyToRealm(toDo);
                            if (AppConfig.addToBottom()) {
                                orderList.getToDos().add(managedToDo);
                            } else {
                                orderList.getToDos().add(0, managedToDo);
                            }
                            realm.commitTransaction();

                            mMainView.displayToDos();
                        }
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

        DeletedList deletedList = realm.where(DeletedList.class).findFirst();
        if (deletedList == null) {
            deletedList = new DeletedList();
            deletedList = realm.copyToRealm(deletedList);
        }
        deletedList.getToDos().add(toDo);

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
            RealmResults<ToDo> localToDos = realm.where(ToDo.class).findAll();
            ArrayList<ToDo> noOrderList = new ArrayList<>();
            for (ToDo toDo : localToDos) {
                noOrderList.add(toDo);
            }
            for (String id : orders) {
                ToDo toDo = localToDos.where().equalTo("id", id).findFirst();
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
        final CategoryRespInformation information = gson.fromJson(resp, CategoryRespInformation.class);
        if (information.isModified()) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    OrderedCateList orderedCateList = new OrderedCateList();
                    RealmList<ToDoCategory> toDoCategories = orderedCateList.getCates();
                    for (ToDoCategory cate : information.getCates()) {
                        toDoCategories.add(cate);
                    }
                    realm.copyToRealmOrUpdate(orderedCateList);
                }
            });
        } else {
            //// TODO: 12/24/2016  handle not modified situation
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
