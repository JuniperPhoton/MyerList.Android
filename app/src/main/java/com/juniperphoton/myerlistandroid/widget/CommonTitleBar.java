package com.juniperphoton.myerlistandroid.widget;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.util.ContextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonTitleBar extends FrameLayout {

    @BindView(R.id.back_iv)
    View backView;

    @BindView(R.id.title_tv)
    TextView textView;

    public CommonTitleBar(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.common_title_bar, this, true);

        ButterKnife.bind(this);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        String title = array.getString(R.styleable.CommonTitleBar_title);
        textView.setText(title);
        array.recycle();

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = ContextUtil.getActivity(view);
                if (activity != null) {
                    activity.finish();
                }
            }
        });
    }
}
