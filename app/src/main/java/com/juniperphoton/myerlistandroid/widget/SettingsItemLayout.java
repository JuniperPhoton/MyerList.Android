package com.juniperphoton.myerlistandroid.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsItemLayout extends FrameLayout {

    @BindView(R.id.settings_item_title)
    TextView titleTextView;

    @BindView(R.id.settings_item_content)
    TextView contentTextView;

    @BindView(R.id.settings_item_switch)
    CompoundButton compoundButton;

    @BindView(R.id.divider_view)
    View dividerView;

    public SettingsItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.row_settings_item, this, true);

        ButterKnife.bind(this);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingsItemLayout);
        String title = array.getString(R.styleable.SettingsItemLayout_setting_title);
        String content = array.getString(R.styleable.SettingsItemLayout_setting_content);
        boolean hasCheckbox = array.getBoolean(R.styleable.SettingsItemLayout_has_checkbox, false);
        boolean showDivider = array.getBoolean(R.styleable.SettingsItemLayout_show_divider, true);
        array.recycle();

        if (title != null) {
            titleTextView.setText(title);
        }

        if (content != null) {
            contentTextView.setText(content);
        }

        if (!hasCheckbox) {
            compoundButton.setVisibility(GONE);
        }

        if (!showDivider) {
            dividerView.setVisibility(GONE);
        }
    }

    public void setOnCheckedListener(AppCompatCheckBox.OnCheckedChangeListener onCheckedListener) {
        compoundButton.setOnCheckedChangeListener(onCheckedListener);
    }

    public void setChecked(boolean checked) {
        compoundButton.setChecked(checked);
    }

    public boolean getChecked() {
        return compoundButton.isChecked();
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setContent(String content) {
        contentTextView.setText(content);
    }
}

