package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName
import com.juniperphoton.myerlist.model.ToDo

import java.util.ArrayList

class GetToDosResponse : CommonResponse() {
    @SerializedName("ScheduleInfo")
    val toDos: ArrayList<ToDo>? = null
}