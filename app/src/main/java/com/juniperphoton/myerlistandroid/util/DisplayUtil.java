package com.juniperphoton.myerlistandroid.util;


import android.content.Context;

public class DisplayUtil {
    public static float getDpi(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDimenInPixel(int valueinDP, Context context) {
        return (int) (valueinDP * getDpi(context));
    }
}
