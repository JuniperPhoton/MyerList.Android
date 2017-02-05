package com.juniperphoton.myerlistandroid.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;

import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.util.LocalSettingUtil;
import com.juniperphoton.myerlistandroid.util.Params;
import com.juniperphoton.myerlistandroid.widget.SettingsItemLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("UnusedDeclaration")
public class SettingsActivity extends BaseActivity {

    @BindView(R.id.setting_item_add_to_bottom)
    SettingsItemLayout mAddToBottomLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        if (LocalSettingUtil.checkKey(this, Params.SETTING_ADD_TO_BOTTOM_KEY)) {
            boolean addToBottom = LocalSettingUtil.getBoolean(this, Params.SETTING_ADD_TO_BOTTOM_KEY, true);
            mAddToBottomLayout.setChecked(addToBottom);
        } else {
            mAddToBottomLayout.setChecked(true);
        }
        mAddToBottomLayout.setOnCheckedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalSettingUtil.putBoolean(SettingsActivity.this, Params.SETTING_ADD_TO_BOTTOM_KEY, isChecked);
            }
        });
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
