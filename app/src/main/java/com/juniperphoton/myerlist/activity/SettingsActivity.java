package com.juniperphoton.myerlist.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.juniperphoton.myerlist.App;
import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.util.LocalSettingUtil;
import com.juniperphoton.myerlist.widget.SettingsItemLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("UnusedDeclaration")
public class SettingsActivity extends BaseActivity {
    @BindView(R.id.settings_item_change_lang)
    SettingsItemLayout mChangeLangView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mChangeLangView.setCallback(new SettingsItemLayout.Callback() {
            @Override
            public void onClick() {
                onClickChangeLanguage();
            }
        });
        updateLocal();
    }

    private void updateLocal() {
        final Resources resources = getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        final Configuration config = resources.getConfiguration();
        Locale locale = config.locale;
        if (locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            mChangeLangView.setContent(getResources().getString(R.string.change_lang_hint_chinese));
        } else {
            mChangeLangView.setContent(getResources().getString(R.string.change_lang_hint_english));
        }
    }

    void onClickChangeLanguage() {
        final Resources resources = getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        final Configuration config = resources.getConfiguration();
        Locale locale = config.locale;
        String[] opts = resources.getStringArray(R.array.language);
        int defaultIndex = 0;
        if (locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            defaultIndex = 1;
        }
        Log.d("settings", locale.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int finalDefaultIndex = defaultIndex;
        builder.setTitle(R.string.change_lang)
                .setSingleChoiceItems(opts, defaultIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (finalDefaultIndex != which) {
                            config.setLocale(which == 0 ? Locale.ENGLISH : Locale.SIMPLIFIED_CHINESE);
                            resources.updateConfiguration(config, dm);
                            recreate();
                        }
                    }
                })
                .create().show();
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
