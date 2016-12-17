package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlistandroid.model.User;

@SuppressWarnings("UnusedDeclaration")
public class LoginResponse extends CommonResponse {
    @SerializedName("UserInfo")
    private User mUser;

    public LoginResponse() {

    }

    public User getUser() {
        return mUser;
    }
}
