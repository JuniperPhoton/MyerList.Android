package com.juniperphoton.myerlist.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.model.ToDoCategory;
import com.juniperphoton.myerlist.realm.RealmUtils;
import com.juniperphoton.myerlist.util.DisplayUtil;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectCategoryView extends LinearLayout implements ViewGroup.OnClickListener {
    private static final String TAG = "SelectCategoryView";

    private Context mContext;
    private OnSelectionChangedCallback mCallback;
    private int mSelectedIndex = 0;

    public SelectCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setOnSelectionChangedCallback(OnSelectionChangedCallback callback) {
        mCallback = callback;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void invokeCallback() {
        if (mCallback != null) {
            mCallback.onSelectionChanged(mSelectedIndex);
        }
    }

    public void setSelected(int index) {
        mSelectedIndex = index;
        updateUi((CateCircleView) getChildAt(mSelectedIndex));
        invokeCallback();
    }

    public void toggleSelection(boolean plus) {
        Log.d(TAG, "toggleSelection:" + plus);
        int target = mSelectedIndex + (plus ? 1 : -1);
        if (target >= getChildCount()) target = 0;
        if (target < 0) target = getChildCount() - 1;
        mSelectedIndex = target;
        updateUi((CateCircleView) getChildAt(mSelectedIndex));
        invokeCallback();
    }

    public void makeViews() {
        removeAllViews();
        Realm realm = RealmUtils.getMainInstance();
        realm.beginTransaction();
        RealmResults<ToDoCategory> categories = realm.where(ToDoCategory.class).findAll();
        for (ToDoCategory toDoCategory : categories) {
            if (toDoCategory.getId() >= 0) {
                CateCircleView circleView = new CateCircleView(mContext, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        DisplayUtil.getDimenInPixel(24, mContext),
                        DisplayUtil.getDimenInPixel(24, mContext));
                layoutParams.setMargins(DisplayUtil.getDimenInPixel(8, mContext), 0, 0, 0);
                circleView.setLayoutParams(layoutParams);
                circleView.setColor(toDoCategory.getIntColor());
                circleView.setOnClickListener(this);
                addView(circleView);
            }
        }
        CateCircleView circleView = new CateCircleView(mContext, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                DisplayUtil.getDimenInPixel(24, mContext),
                DisplayUtil.getDimenInPixel(24, mContext));
        circleView.setLayoutParams(layoutParams);
        circleView.setColor(ContextCompat.getColor(mContext, R.color.MyerListBlue));
        circleView.setOnClickListener(this);
        addView(circleView, 0);
        getChildAt(0).setSelected(true);
        realm.commitTransaction();
    }

    @Override
    public void onClick(View v) {
        updateUi((CateCircleView) v);
        invokeCallback();
    }

    private void updateUi(CateCircleView circleView) {
        for (int i = 0; i < getChildCount(); i++) {
            CateCircleView cateCircleView = (CateCircleView) getChildAt(i);
            if (cateCircleView == circleView) {
                cateCircleView.setSelected(true);
                mSelectedIndex = i;
            } else {
                cateCircleView.setSelected(false);
            }
        }
    }

    public interface OnSelectionChangedCallback {
        void onSelectionChanged(int position);
    }
}
