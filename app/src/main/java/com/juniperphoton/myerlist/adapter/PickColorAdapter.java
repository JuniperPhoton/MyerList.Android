package com.juniperphoton.myerlist.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.widget.CircleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PickColorAdapter extends BaseAdapter<Integer, PickColorAdapter.PickColorHolder> {
    private Context mContext;
    private Callback mCallback;

    public PickColorAdapter(Context context) {
        mContext = context;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected PickColorHolder onCreateItemViewHolder(ViewGroup parent) {
        return new PickColorHolder(LayoutInflater.from(mContext).inflate(R.layout.row_pick_color, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(PickColorHolder holder, int dataPosition) {
        holder.bind(getData(dataPosition));
        animateContainer(holder.rootView, dataPosition);
    }

    private void animateContainer(final View container, int position) {
        int delay = 30 * (position);
        int duration = 700;

        container.setAlpha(0f);
        container.setTranslationX(60);

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                container.setAlpha((float) valueAnimator.getAnimatedValue());
            }
        });
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.start();

        ValueAnimator animator2 = ValueAnimator.ofInt(300, 0);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                container.setTranslationX((int) valueAnimator.getAnimatedValue());
            }
        });
        animator2.setInterpolator(new DecelerateInterpolator());
        animator2.setStartDelay(delay);
        animator2.setDuration(duration);
        animator2.start();
    }

    class PickColorHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.color_view)
        View mTintView;

        Integer mColor;

        PickColorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mTintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null && mColor != null) {
                        mCallback.onSelectColor(mColor);
                    }
                }
            });
        }

        public void bind(Integer color) {
            mTintView.setBackgroundColor(color);
            mColor = color;
        }
    }

    public interface Callback {
        void onSelectColor(int color);
    }
}
