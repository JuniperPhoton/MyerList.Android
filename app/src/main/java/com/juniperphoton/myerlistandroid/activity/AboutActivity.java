package com.juniperphoton.myerlistandroid.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.util.PackageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;

@SuppressWarnings("UnusedDeclaration")
public class AboutActivity extends BaseActivity {

    @BindView(R.id.version_tv)
    TextView mVersionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        updateVersion();
    }

    private void updateVersion() {
        mVersionTextView.setText("Version " + PackageUtil.getVersionName(this));
    }

    @OnClick(R.id.email_rl)
    void emailClick(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dengweichao@hotmail.com"}); // recipients

        String SHARE_SUBJECT = "MyerList for Android %s feedback";
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(SHARE_SUBJECT, PackageUtil.getVersionName(this)));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(emailIntent);
    }

    @OnClick(R.id.activity_about_rate_rl)
    void rateClick(View view) {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.activity_about_donate_rl)
    void donateClick(View view) {
        if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
            AlipayZeroSdk.startAlipayClient(this, "aex09127b4dbo4o7fbvcyb0");
        }
    }
}
