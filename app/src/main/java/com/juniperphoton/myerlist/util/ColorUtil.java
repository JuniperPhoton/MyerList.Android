package com.juniperphoton.myerlist.util;


import android.graphics.Color;

public class ColorUtil {
    public static int makeColorDarker(int originalColor) {
        int red = Color.red(originalColor);
        int green = Color.green(originalColor);
        int blue = Color.blue(originalColor);
        return Color.argb(255, (int) (red * 0.7), (int) (green * 0.7), (int) (blue * 0.7));
    }

    public static String fromInt(int originalColor) {
        return String.format("#%06X", 0xFFFFFF & originalColor);
    }
}
