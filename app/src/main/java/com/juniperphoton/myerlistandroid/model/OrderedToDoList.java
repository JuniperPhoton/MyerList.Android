package com.juniperphoton.myerlistandroid.model;


import com.juniperphoton.myerlistandroid.util.AppConfig;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderedToDoList extends RealmObject {
    @PrimaryKey
    private String id;

    public OrderedToDoList() {
        id = AppConfig.getSid();
    }

    public RealmList<ToDo> getToDos() {
        return toDos;
    }

    public void setToDos(RealmList<ToDo> toDos) {
        this.toDos = toDos;
    }

    private RealmList<ToDo> toDos;
}
