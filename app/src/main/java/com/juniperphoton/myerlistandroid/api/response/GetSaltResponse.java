package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration")
public class GetSaltResponse extends CommonResponse {
    @SerializedName("Salt")
    private String mSalt;

    public GetSaltResponse() {

    }

    public String getSalt() {
        return mSalt;
    }
}
