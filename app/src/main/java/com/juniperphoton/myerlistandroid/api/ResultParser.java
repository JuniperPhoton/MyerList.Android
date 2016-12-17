package com.juniperphoton.myerlistandroid.api;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

@SuppressWarnings("UnusedDeclaration")
public class ResultParser {
    public static boolean isUserExist(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            boolean exist = jsonObject.getBoolean("isExist");
            return exist;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getSalt(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            String salt = jsonObject.getString("Salt");
            return salt;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
