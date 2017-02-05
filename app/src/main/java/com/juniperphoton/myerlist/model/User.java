package com.juniperphoton.myerlist.model;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@SuppressWarnings("UnusedDeclaration")
public class User extends RealmObject {
    @SerializedName("sid")
    @PrimaryKey
    private int mSid;

    private String mEmail;

    @SerializedName("access_token")
    private String mAccessToken;

    public User() {

    }

    public int getSID() {
        return this.mSid;
    }

    public void setSID(int value) {
        this.mSid = value;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public void setEmail(String value) {
        this.mEmail = value;
    }

    public String getAccessToken() {
        return mAccessToken;
    }
}
