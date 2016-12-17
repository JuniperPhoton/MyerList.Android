package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration")
public class CommonResponse {
    @SerializedName("isSuccessed")
    protected boolean mIsSuccessed;

    @SerializedName("error_code")
    protected int mErrorCode;

    @SerializedName("error_msg")
    protected String mErrorMsg;

    public CommonResponse() {

    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public boolean getOK() {
        return mIsSuccessed;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getFriendErrorMessage() {
        switch (mErrorCode) {
            case ErrorCodes.API_ERROR_USER_NOTEXIST:
                return "The user is not existed";
            case ErrorCodes.API_ERROR_USER_ALEADY_EXIST:
                return "The email has been registered";
            case ErrorCodes.API_ERROR_CHECK_USER_NAME_OR_PWD:
                return "Please check your email and password";
        }
        return "Error happens. Code " + mErrorCode;
    }

    public class ErrorCodes {
        //common error define
        public static final int API_ERROR_DATABASE_ERROR = 100;

        public static final int API_ERROR_ACTION_NOTEXIST = 101;

        public static final int API_ERROR_ACCESS_TOKEN_INVAID = 102;

        //user
        public static final int API_ERROR_USER_NOTEXIST = 200;

        public static final int API_ERROR_PARM_LACK = 202;

        public static final int API_ERROR_USER_ALEADY_EXIST = 203;

        public static final int API_ERROR_USER_NOT_EXIST = 204;

        public static final int API_ERROR_CHECK_USER_NAME_OR_PWD = 205;

        //schedule
        public static final int API_ERROR_LACK_PARAM = 300;

        public static final int API_ERROR_SCHEDULE_NOT_EXIST = 301;


    }
}
