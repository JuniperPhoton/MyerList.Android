package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlistandroid.model.ToDo;

import java.util.ArrayList;

public class GetToDosResponse extends CommonResponse {
    @SerializedName("ScheduleInfo")
    private ArrayList<ToDo> mToDos;

    public GetToDosResponse() {

    }

    public ArrayList<ToDo> getToDos() {
        return mToDos;
    }
}
