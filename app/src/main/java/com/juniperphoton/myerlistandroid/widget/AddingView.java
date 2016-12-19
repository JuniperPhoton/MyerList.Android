package com.juniperphoton.myerlistandroid.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddingView extends FrameLayout implements View.OnTouchListener {

    private static final float FLING_THRESHOULD = 20f;

    @BindView(R.id.adding_view_root)
    RelativeLayout mRoot;

    @BindView(R.id.adding_view_add_content)
    EditText mEditText;

    @BindView(R.id.adding_view_cate)
    TextView mCateName;

    @BindView(R.id.select_category_view)
    SelectCategoryView mSelectCategoryView;

    private Context mContext;

    private AddingViewCallback mCallback;

    private String mInputText;

    public AddingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_adding, this, true);
        ButterKnife.bind(this);

        mRoot.setOnTouchListener(this);
    }

    @OnClick(R.id.adding_view_ok)
    void onClickOk() {
        prepareToHide();
        if (mCallback != null) {
            mCallback.onClickOk(mSelectCategoryView.getSelectedIndex(), mInputText);
        }
    }

    @OnClick(R.id.adding_view_cancel)
    void onClickCancel() {
        prepareToHide();
        if (mCallback != null) {
            mCallback.onClickCancel();
        }
    }

    private void prepareToHide() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        mInputText = mEditText.getEditableText().toString();
        mEditText.setText("");
    }

    public void setCallback(AddingViewCallback callback) {
        mCallback = callback;
    }

    public void setSelected(int index) {
        mSelectCategoryView.setSelected(index);
    }

    public void setOnSelectionChangedCallback(SelectCategoryView.OnSelectionChangedCallback callback) {
        mSelectCategoryView.setOnSelectionChangedCallback(callback);
    }

    public void updateCategory(ToDoCategory category) {
        mCateName.setText(category.getName());
        animateBackground(category.getIntColor());
    }

    public void showInputPane() {
        mEditText.requestFocus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mEditText, 0);
            }
        }, 50);
    }

    public void makeCategoriesSelection() {
        mSelectCategoryView.makeViews();
    }

    private void animateBackground(int toColor) {
        ColorDrawable colorDrawable = (ColorDrawable) mRoot.getBackground();
        int from = colorDrawable.getColor();

        ValueAnimator valueAnimator = ValueAnimator.ofArgb(from, toColor);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRoot.setBackground(new ColorDrawable((int) animation.getAnimatedValue()));
            }
        });
        valueAnimator.start();
    }

    float startX;
    float dx;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                dx = event.getX() - startX;
                break;
            case MotionEvent.ACTION_UP:
                if (dx > FLING_THRESHOULD) {
                    mSelectCategoryView.toggleSelection(false);
                } else if (dx < -FLING_THRESHOULD) {
                    mSelectCategoryView.toggleSelection(true);
                }
                startX = 0;
                dx = 0;
                break;
        }
        return true;
    }

    public interface AddingViewCallback {
        void onClickOk(int cateindex, String content);

        void onClickCancel();
    }
}
