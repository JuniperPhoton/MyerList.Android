package com.juniperphoton.myerlistandroid.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.api.APIException;
import com.juniperphoton.myerlistandroid.api.CloudService;
import com.juniperphoton.myerlistandroid.api.response.AddToDoResponse;
import com.juniperphoton.myerlistandroid.api.response.CateResponse;
import com.juniperphoton.myerlistandroid.api.response.CommonResponse;
import com.juniperphoton.myerlistandroid.api.response.GetOrderResponse;
import com.juniperphoton.myerlistandroid.api.response.GetToDosResponse;
import com.juniperphoton.myerlistandroid.model.CategoryRespInformation;
import com.juniperphoton.myerlistandroid.model.OrderedCateList;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.realm.RealmUtils;
import com.juniperphoton.myerlistandroid.util.AppConfig;
import com.juniperphoton.myerlistandroid.util.ToastService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    private MainContract.View mView;

    public MainPresenter(MainContract.View mainView) {
        mView = mainView;
    }

    @Override
    public void getCates() {
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
                        mView.displayCategories();
                        getToDos();
                    }
                });
    }

    @Override
    public void getToDos() {
        CloudService.getInstance().getToDos()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<GetToDosResponse, Observable<GetOrderResponse>>() {
                    @Override
                    public Observable<GetOrderResponse> call(final GetToDosResponse toDoResponse) {
                        if (toDoResponse.getToDos() != null) {
                            mergeToDos(toDoResponse.getToDos());
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

    private void mergeToDos(List<ToDo> toDos) {
        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        RealmResults<ToDo> shouldDeleted = realm.where(ToDo.class)
                .notEqualTo(ToDo.DELETED_KEY, Boolean.TRUE).findAll();
        for (ToDo toDo : shouldDeleted) {
            toDo.deleteFromRealm();
        }
        for (ToDo toDo : toDos) {
            realm.copyToRealmOrUpdate(toDo);
        }
        realm.commitTransaction();

        Log.d(TAG, "got to-dos");
    }

    @Override
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

    @Override
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

    @Override
    public void modifyToDo(final String cate, final String content, final String id) {
        final String dateStr = getDateStr();
        Realm realm = RealmUtils.getMainInstance();
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
                            ToastService.sendShortToast(App.getInstance().getString(R.string.modified_hint));
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

    @Override
    public void addToDo(String cate, String content) {
        String dateStr = getDateStr();
        CloudService.getInstance().addToDo(dateStr, content, ToDo.IS_NOT_DONE, cate)
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
                            Realm realm = RealmUtils.getMainInstance();
                            realm.beginTransaction();

                            ToDo managedToDo = realm.copyToRealm(toDo);

                            if (AppConfig.addToBottom()) {
                                int pos = realm.where(ToDo.class).findAll().max(ToDo.POSITION_KEY).intValue();
                                managedToDo.setPosition(++pos);
                            } else {
                                int pos = realm.where(ToDo.class).findAll().min(ToDo.POSITION_KEY).intValue();
                                managedToDo.setPosition(--pos);
                            }
                            realm.commitTransaction();

                            mView.displayToDos();
                            mView.uploadOrders();
                        }
                    }
                });
    }

    @Override
    public void deleteToDo(ToDo toDo) {
        String id = toDo.getId();

        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        toDo.setDeleted(true);
        realm.commitTransaction();

        mView.notifyToDoDeleted(toDo.getPosition());

        CloudService.getInstance().deleteToDo(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonResponse>() {
                    @Override
                    public void call(CommonResponse commonResponse) {
                        Log.d(TAG, "deleteToDo");
                    }
                });
    }

    @Override
    public void clearDeletedList() {
        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        RealmResults<ToDo> deletedList = realm.where(ToDo.class).equalTo(ToDo.DELETED_KEY, true)
                .findAll();
        for (ToDo todo : deletedList) {
            todo.deleteFromRealm();
        }
        realm.commitTransaction();

        mView.displayToDos();
    }

    @Override
    public void recoverToDo(ToDo toDo) {
        addToDo(toDo.getCate(), toDo.getContent());
        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        toDo.deleteFromRealm();
        realm.commitTransaction();
        mView.displayToDos();
    }

    private void orderToDos(GetOrderResponse getOrderResponse) {
        String order = getOrderResponse.getOrder();
        if (order != null) {
            final String[] orders = order.split(",");

            Realm realm = RealmUtils.getMainInstance();
            realm.beginTransaction();

            RealmResults<ToDo> localToDos = realm.where(ToDo.class).findAll();
            for (int i = 0; i < orders.length; i++) {
                ToDo toDo = localToDos.where().equalTo(ToDo.ID_KEY, orders[i]).findFirst();
                if (toDo != null) {
                    toDo.setPosition(i);
                }
            }

            realm.commitTransaction();
            ToastService.sendShortToast(App.getInstance().getString(R.string.fetch_hint));
            mView.displayToDos();
        } else {
            Log.d(TAG, "Can't get order.");
        }
    }

    private void parseCategories(String resp) {
        Gson gson = new Gson();
        final CategoryRespInformation information = gson.fromJson(resp, CategoryRespInformation.class);
        if (information.isModified()) {
            RealmUtils.getMainInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    OrderedCateList orderedCateList = new OrderedCateList();
                    RealmList<ToDoCategory> toDoCategories = orderedCateList.getCates();
                    for (ToDoCategory cate : information.getCates()) {
                        toDoCategories.add(cate);
                    }
                    RealmUtils.getMainInstance().copyToRealmOrUpdate(orderedCateList);
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
