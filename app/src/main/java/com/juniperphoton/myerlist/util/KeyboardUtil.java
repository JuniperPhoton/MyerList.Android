package com.juniperphoton.myerlist.util;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {
    public static void show(Context context, final View view, int delay) {
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.showSoftInput(view, 0);
            }
        }, delay);
    }

    public static void hide(Context context, IBinder binder) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binder, 0);
    }
}
