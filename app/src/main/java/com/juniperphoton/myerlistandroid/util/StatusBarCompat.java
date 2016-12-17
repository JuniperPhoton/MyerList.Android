package com.juniperphoton.myerlistandroid.util;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class StatusBarCompat {
    public static void setUpActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && !isChrome()) {
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    );
        }

        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static boolean isChrome() {
        return Build.BRAND == "chromium" || Build.BRAND == "chrome";
    }

}
