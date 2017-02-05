package com.juniperphoton.myerlist.api.response;


import com.google.gson.annotations.SerializedName;

public class CheckUserResponse extends CommonResponse {
    @SerializedName("isExist")
    private boolean mIsExist;

    public CheckUserResponse() {

    }

    public boolean getExist() {
        return super.mIsSuccessed && mIsExist;
    }
}
