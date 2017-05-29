package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName

class GetSaltResponse : CommonResponse() {
    @SerializedName("Salt")
    val salt: String? = null
}