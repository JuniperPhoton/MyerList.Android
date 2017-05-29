package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName

class CheckUserResponse : CommonResponse() {
    @SerializedName("isExist")
    private val isExist: Boolean = false

    val exist: Boolean
        get() = super.ok && isExist
}