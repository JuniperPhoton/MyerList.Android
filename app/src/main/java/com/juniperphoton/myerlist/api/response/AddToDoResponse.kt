package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName
import com.juniperphoton.myerlist.model.ToDo

class AddToDoResponse : CommonResponse() {
    @SerializedName("ScheduleInfo")
    val toDo: ToDo? = null
}