package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlistandroid.model.MyerUser;

@SuppressWarnings("UnusedDeclaration")
public class LoginResponse extends CommonResponse {
    @SerializedName("UserInfo")
    private MyerUser mUser;

    public LoginResponse() {

    }

    public MyerUser getUser() {
        return mUser;
    }
}
