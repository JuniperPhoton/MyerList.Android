package com.juniperphoton.myerlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.juniperphoton.myerlist.R;

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
        int delay = 10 * (position);
        int duration = 400;

        float startAlpha = 0f;
        int startTranslation = 40;

        container.setAlpha(startAlpha);
        container.setTranslationX(startTranslation);

        container.animate().alpha(1.0f)
                .translationX(0)
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    protected class PickColorHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.color_view)
        View mTintView;

        @BindView(R.id.color_picker_item_root)
        ViewGroup mRoot;

        Integer mColor;

        PickColorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mRoot.setOnClickListener(new View.OnClickListener() {
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
