package com.juniperphoton.myerlistandroid.model;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration")
public class MyerUser {
    @SerializedName("sid")
    private int mSid;

    private String mEmail;

    @SerializedName("access_token")
    private String mAccessToken;

    public MyerUser() {

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

    public String getAccessToken(){
        return mAccessToken;
    }
}
