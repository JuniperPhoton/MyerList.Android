package com.juniperphoton.myerlist.api.response;


import com.google.gson.annotations.SerializedName;
import com.juniperphoton.myerlist.model.User;

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
