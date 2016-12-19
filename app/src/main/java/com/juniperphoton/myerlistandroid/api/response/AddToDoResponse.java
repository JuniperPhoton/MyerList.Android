package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlistandroid.model.ToDo;

public class AddToDoResponse extends CommonResponse {
    @SerializedName("ScheduleInfo")
    private ToDo mToDo;

    public ToDo getToDo() {
        return mToDo;
    }
}
