package com.juniperphoton.myerlist.model;


import com.juniperphoton.myerlist.util.AppConfig;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderedCateList extends RealmObject {
    @PrimaryKey
    private String id;

    public OrderedCateList() {
        id = AppConfig.getSid();
        cates = new RealmList<>();
    }

    public RealmList<ToDoCategory> getCates() {
        return cates;
    }

    public void setCates(RealmList<ToDoCategory> toDos) {
        this.cates = toDos;
    }

    private RealmList<ToDoCategory> cates;
}
