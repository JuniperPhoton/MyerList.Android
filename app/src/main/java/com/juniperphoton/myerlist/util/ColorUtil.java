package com.juniperphoton.myerlist.util;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Integer> generateColors() {
        List<Integer> list = new ArrayList<>();
        list.add(Color.parseColor("#F75B44"));
        list.add(Color.parseColor("#EC4128"));
        list.add(Color.parseColor("#F73215"));
        list.add(Color.parseColor("#F7445B"));
        list.add(Color.parseColor("#E1184B"));
        list.add(Color.parseColor("#C11943"));
        list.add(Color.parseColor("#80224C"));
        list.add(Color.parseColor("#66436F"));
        list.add(Color.parseColor("#713A80"));
        list.add(Color.parseColor("#5F3A80"));
        list.add(Color.parseColor("#4D3A80"));
        list.add(Color.parseColor("#352F44"));
        list.add(Color.parseColor("#474E88"));
        list.add(Color.parseColor("#2E3675"));
        list.add(Color.parseColor("#2A2E51"));
        list.add(Color.parseColor("#417C98"));
        list.add(Color.parseColor("#6FD1FF"));
        list.add(Color.parseColor("#3CBBF7"));
        list.add(Color.parseColor("#217CDC"));
        list.add(Color.parseColor("#4CAFFF"));
        list.add(Color.parseColor("#5474C1"));
        list.add(Color.parseColor("#317CA0"));
        list.add(Color.parseColor("#39525F"));
        list.add(Color.parseColor("#4F9595"));
        list.add(Color.parseColor("#2C8D8D"));
        list.add(Color.parseColor("#00BEBE"));
        list.add(Color.parseColor("#257575"));
        list.add(Color.parseColor("#2B8A78"));
        list.add(Color.parseColor("#3FBEA6"));
        list.add(Color.parseColor("#3FBE7D"));
        list.add(Color.parseColor("#1C9B5A"));
        list.add(Color.parseColor("#5A9849"));
        list.add(Color.parseColor("#739849"));
        list.add(Color.parseColor("#C9D639"));
        list.add(Color.parseColor("#D6CD00"));
        list.add(Color.parseColor("#F7C142"));
        list.add(Color.parseColor("#F7D842"));
        list.add(Color.parseColor("#F79E42"));
        list.add(Color.parseColor("#FF8726"));
        list.add(Color.parseColor("#EF7919"));
        return list;
    }
}
