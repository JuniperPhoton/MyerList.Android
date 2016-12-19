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
        toDos = new RealmList<>();
    }

    public RealmList<ToDo> getToDos() {
        return toDos;
    }

    public void setToDos(RealmList<ToDo> toDos) {
        this.toDos = toDos;
    }

    private RealmList<ToDo> toDos;
}
