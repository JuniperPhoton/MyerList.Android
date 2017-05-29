package com.juniperphoton.myerlist.model

import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {
    @SerializedName("sid")
    @PrimaryKey
    var sid: Int = 0

    var email: String? = null

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("Salt")
    var salt: String? = null
}