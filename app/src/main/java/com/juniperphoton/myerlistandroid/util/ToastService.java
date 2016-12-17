package com.juniperphoton.myerlistandroid.util;


import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;

public class ToastService {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void sendShortToast(final String str) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sendInternal(str);
                }
            });
        } else {
            sendInternal(str);
        }
    }

    private static void sendInternal(String str) {
        LayoutInflater inflater = LayoutInflater.from(App.getInstance());
        View view = inflater.inflate(R.layout.toast_layout, null);

        TextView textView = (TextView) view.findViewById(R.id.toast_textView);
        textView.setText(str);

        Toast toast = new Toast(App.getInstance());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.show();
    }
}
