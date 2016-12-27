package com.juniperphoton.myerlistandroid.model;


import com.juniperphoton.myerlistandroid.util.AppConfig;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DeletedList extends RealmObject {
    @PrimaryKey
    private String id;

    public DeletedList() {
        id = AppConfig.getSid();
    }

    public RealmList<ToDo> getToDos() {
        return toDos;
    }

    public String getId() {
        return id;
    }

    private RealmList<ToDo> toDos = new RealmList<>();
}
