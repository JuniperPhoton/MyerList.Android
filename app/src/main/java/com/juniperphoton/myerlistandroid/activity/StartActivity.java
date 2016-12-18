package com.juniperphoton.myerlistandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.util.Params;

import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("UnusedDeclaration")
public class StartActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_btn)
    void onClickLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Params.LOGIN_MODE, Params.LoginMode.LOGIN);
        startActivity(intent);
    }

    @OnClick(R.id.register_btn)
    void onClickRegister() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Params.LOGIN_MODE, Params.LoginMode.REGISTER);
        startActivity(intent);
    }

    @OnClick(R.id.offline_btn)
    void onClickOffline() {

    }
}
