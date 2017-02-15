package com.juniperphoton.myerlist.util;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {
    public static void hide(Context context, IBinder binder) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binder, 0);
    }
}
