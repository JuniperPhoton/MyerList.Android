package com.juniperphoton.myerlist.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;

import com.juniperphoton.myerlist.App;
import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.util.LocalSettingUtil;
import com.juniperphoton.myerlist.util.Params;
import com.juniperphoton.myerlist.widget.SettingsItemLayout;

import butterknife.BindView;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirm_to_logout))
                .setPositiveButton(getString(R.string.confirm_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocalSettingUtil.clearAll(App.getInstance());
                        Intent intent = new Intent(App.getInstance(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.confirm_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
