package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName

open class CommonResponse {
    object ErrorCodes {
        //common error define
        val API_ERROR_DATABASE_ERROR = 100

        val API_ERROR_ACTION_NOTEXIST = 101

        val ADI_ERROR_ACCESS_TOKEN_INLAID = 102

        //user
        val API_ERROR_USER_NOTEXIST = 200

        val API_ERROR_PARM_LACK = 202

        val API_ERROR_USER_ALREADY_EXIST = 203

        val API_ERROR_USER_NOT_EXIST = 204

        val API_ERROR_CHECK_USER_NAME_OR_PWD = 205

        //schedule
        val API_ERROR_LACK_PARAM = 300

        val API_ERROR_SCHEDULE_NOT_EXIST = 301
    }

    @SerializedName("isSuccessed")
    var ok: Boolean = false
        protected set

    @SerializedName("error_code")
    var errorCode: Int = 0
        protected set

    @SerializedName("error_msg")
    var errorMsg: String? = null
        protected set

    val friendErrorMessage: String
        get() {
            when (errorCode) {
                ErrorCodes.API_ERROR_USER_NOTEXIST -> return "The user is not existed"
                ErrorCodes.API_ERROR_USER_ALREADY_EXIST -> return "The email has been registered"
                ErrorCodes.API_ERROR_CHECK_USER_NAME_OR_PWD -> return "Please check your email and password"
                ErrorCodes.API_ERROR_LACK_PARAM -> return "Lack params"
                ErrorCodes.API_ERROR_SCHEDULE_NOT_EXIST -> return "Schedule is not existed"
            }
            return "Error occurs. Code : $errorCode"
        }
}
