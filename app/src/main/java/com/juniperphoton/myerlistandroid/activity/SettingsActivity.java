package com.juniperphoton.myerlistandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.util.LocalSettingUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("UnusedDeclaration")
public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.settings_logout)
    void onClickLogout() {
        LocalSettingUtil.clearAll(this);

        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
