package com.juniperphoton.myerlist.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.model.ToDoCategory;
import com.juniperphoton.myerlist.realm.RealmUtils;
import com.juniperphoton.myerlist.util.CustomItemTouchHelper;
import com.juniperphoton.myerlist.util.KeyboardUtil;
import com.juniperphoton.myerlist.widget.CircleView;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CustomCategoryAdapter extends BaseAdapter<ToDoCategory, CustomCategoryAdapter.CustomCategoryViewHolder> {
    private Context mContext;
    private Callback mCallback;

    private CustomItemTouchHelper helper = new CustomItemTouchHelper(new CustomItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = CustomItemTouchHelper.UP | CustomItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int targetPos = target.getAdapterPosition() - (hasHeader() ? 1 : 0);
            int pos = viewHolder.getAdapterPosition() - (hasHeader() ? 1 : 0);
            if (hasHeader() && targetPos < 0) {
                return false;
            }
            Collections.swap(getData(), pos, targetPos);
            notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            mCallback.onArrangeCompleted();
            return false;
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
        }
    });

    public CustomCategoryAdapter(Context context) {
        mContext = context;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected CustomCategoryViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new CustomCategoryViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.row_custom_category, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(CustomCategoryViewHolder holder, int dataPosition) {
        holder.bind(getData(dataPosition));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        helper.attachToRecyclerView(recyclerView);
    }

    class CustomCategoryViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.row_category_color_view_root)
        View mColorRoot;

        @BindView(R.id.row_category_color_view)
        CircleView mCateView;

        @BindView(R.id.row_category_content)
        EditText mEditTextView;

        @BindView(R.id.arrange_delete)
        View mDeleteView;

        @BindView(R.id.arrange_thumb)
        View mThumb;

        private ToDoCategory mCategory;

        CustomCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeData(getAdapterPosition() - 1);
                    if (mCallback != null) {
                        mCallback.onClickDelete(mCategory);
                    }
                }
            });

            mColorRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickSelectCategory(mCategory);
                    }
                }
            });

            mThumb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            helper.startDrag(CustomCategoryViewHolder.this);
                            mEditTextView.clearFocus();
                            KeyboardUtil.hide(mContext, mEditTextView.getWindowToken());
                            return true;
                    }
                    return true;
                }
            });
        }

        private void bind(final ToDoCategory toDoCategory) {
            mCategory = toDoCategory;
            mCateView.setColor(mCategory.getIntColor());
            mEditTextView.setText(mCategory.getName());
            mEditTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mCategory.setName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public interface Callback {
        void onClickSelectCategory(ToDoCategory category);

        void onClickDelete(ToDoCategory category);

        void onArrangeCompleted();
    }
}
