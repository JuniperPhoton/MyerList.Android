package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlistandroid.model.ToDo;

import java.util.ArrayList;

public class ToDoResponse extends CommonResponse {
    @SerializedName("ScheduleInfo")
    private ArrayList<ToDo> mToDos;

    public ToDoResponse() {

    }

    public ArrayList<ToDo> getToDos() {
        return mToDos;
    }
}
