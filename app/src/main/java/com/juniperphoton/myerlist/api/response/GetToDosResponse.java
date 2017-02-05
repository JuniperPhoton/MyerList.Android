package com.juniperphoton.myerlist.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlist.model.ToDo;

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
