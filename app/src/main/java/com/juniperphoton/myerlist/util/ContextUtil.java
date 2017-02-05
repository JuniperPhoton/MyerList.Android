package com.juniperphoton.myerlist.util;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

public class ContextUtil {
    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}