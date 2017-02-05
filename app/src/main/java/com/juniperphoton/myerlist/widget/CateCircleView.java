package com.juniperphoton.myerlist.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.juniperphoton.myerlist.R;

public class CateCircleView extends CircleView {
    private boolean mSelected;
    private Context mContext;

    private Paint mDarkPaint = new Paint();
    private Paint mPaint = new Paint();

    private int mRadius = 0;

    public CateCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_borderless));
    }

    @Override
    public void setColor(int color) {
        super.setColor(color);
        mPaint.setColor(color);
        mDarkPaint.setColor(Color.argb(255, (int) (Color.red(color) * 0.4),
                (int) (Color.green(color) * 0.4),
                (int) (Color.blue(color) * 0.4)));
    }

    public void setSelected(final boolean selected) {
        if (mSelected == selected) {
            return;
        }
        mSelected = selected;
        int radius = (int) (getLayoutParams().width / 2 * 0.5);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(selected ? 0 : radius, selected ? radius : 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mDarkPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (int) (getWidth() / 2 * 0.8), mPaint);
        if (mSelected) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mDarkPaint);
        }
    }
}
