package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlistandroid.model.ToDo;

import java.util.ArrayList;

import io.realm.RealmList;

public class ToDoResponse extends CommonResponse {
    @SerializedName("ScheduleInfo")
    private ArrayList<ToDo> mToDos;

    public ToDoResponse() {

    }

    public ArrayList<ToDo> getToDos() {
        return mToDos;
    }
}
