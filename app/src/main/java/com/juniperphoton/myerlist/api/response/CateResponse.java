package com.juniperphoton.myerlist.api.response;


import com.google.gson.annotations.SerializedName;

public class CateResponse extends CommonResponse {
    @SerializedName("Cate_Info")
    private String mRawString;

    public CateResponse() {
    }

    public String getRawString() {
        return mRawString;
    }

    public void setRawString(String rawString) {
        this.mRawString = rawString;
    }
}
