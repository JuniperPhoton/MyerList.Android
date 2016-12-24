package com.juniperphoton.myerlistandroid.util;


import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.HashMap;

public class TypefaceUtil {
    private static HashMap<String, Typeface> typefaceHashMap;

    public static void setTypeFace(TextView textView, String path, Context context) {
        if (typefaceHashMap == null) {
            typefaceHashMap = new HashMap<>();
        }
        Typeface typeface = typefaceHashMap.get(path);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), path);
            typefaceHashMap.put(path, typeface);
        }
        textView.setTypeface(typeface);
    }
}
