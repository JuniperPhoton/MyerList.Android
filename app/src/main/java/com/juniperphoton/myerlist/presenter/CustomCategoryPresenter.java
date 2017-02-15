package com.juniperphoton.myerlist.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.juniperphoton.myerlist.api.CloudService;
import com.juniperphoton.myerlist.api.response.CommonResponse;
import com.juniperphoton.myerlist.model.ToDoCategory;
import com.juniperphoton.myerlist.realm.RealmUtils;
import com.juniperphoton.myerlist.util.ToastService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CustomCategoryPresenter implements CustomCategoryContract.Presenter {
    private CustomCategoryContract.View mView;

    public CustomCategoryPresenter(CustomCategoryContract.View view) {
        mView = view;
    }

    private List<ToDoCategory> mList;

    @Override
    public void cancel() {
        mView.finish();
    }

    @Override
    public void commit() {
        mView.hideKeyboard();

        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        realm.delete(ToDoCategory.class);
        int i = 0;
        for (ToDoCategory category : mList) {
            category.setPosition(i);
            i++;
            realm.copyToRealmOrUpdate(category);
        }
        realm.commitTransaction();
        upload();
    }

    private void upload() {
        JsonArray jsonArray = new JsonArray();
        for (ToDoCategory category : mList) {
            JsonObject object = new JsonObject();
            object.addProperty("name", category.getName());
            object.addProperty("color", category.getColor());
            object.addProperty("id", category.getId());
            jsonArray.add(object);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modified", true);
        jsonObject.add("cates", jsonArray);
        String str = jsonObject.toString();

        CloudService.getInstance().updateToDoCategories(str)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialog();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommonResponse>() {
                    @Override
                    public void onCompleted() {
                        mView.hideDialog(500);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommonResponse commonResponse) {
                        if (commonResponse.getOK()) {
                            ToastService.sendShortToast("Updated");
                            mView.finish();
                        } else {
                            ToastService.sendShortToast("Failed to update category");
                        }
                    }
                });
    }

    @Override
    public void start() {
        refreshData();
    }

    @Override
    public void stop() {

    }

    @Override
    public void refreshData() {
        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        RealmResults<ToDoCategory> realmResults = realm.where(ToDoCategory.class)
                .findAllSorted(ToDoCategory.POSITION_KEY, Sort.ASCENDING);
        realm.commitTransaction();
        mList = new ArrayList<>();

        // Copy them first, every changes will not write into realm db
        for (ToDoCategory category : realmResults) {
            mList.add(category.getCopy());
        }
        mView.initData(mList);
    }
}
