package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName

class CateResponse : CommonResponse() {
    @SerializedName("Cate_Info")
    var rawString: String? = null
}