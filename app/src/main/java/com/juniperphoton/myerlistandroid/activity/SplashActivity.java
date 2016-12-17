package com.juniperphoton.myerlistandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.util.AppConfig;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (AppConfig.hasLogined()) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
