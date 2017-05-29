package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName
import com.juniperphoton.myerlist.model.User

open class LoginResponse : CommonResponse() {
    @SerializedName("UserInfo")
    val user: User? = null
}