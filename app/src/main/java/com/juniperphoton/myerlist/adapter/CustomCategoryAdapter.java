package com.juniperphoton.myerlist.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.model.ToDoCategory;
import com.juniperphoton.myerlist.util.CustomItemTouchHelper;
import com.juniperphoton.myerlist.util.KeyboardUtil;
import com.juniperphoton.myerlist.widget.CircleView;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        TextView mNameTextView;

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
                            return true;
                    }
                    return true;
                }
            });

            mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FrameLayout root = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_name, null, false);
                    final EditText editText = (EditText) root.findViewById(R.id.edit_text_view);

                    if (mCategory != null) {
                        editText.setText(mCategory.getName());
                        editText.setSelection(editText.getText().length());
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(root)
                            .setTitle(R.string.edit_category_name_title)
                            .setPositiveButton(mContext.getString(R.string.ok_adding), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (mCategory != null && mNameTextView != null) {
                                        mCategory.setName(editText.getText().toString());
                                        mNameTextView.setText(editText.getText().toString());
                                    }
                                }
                            })
                            .setNegativeButton(mContext.getString(R.string.cancel_adding), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    KeyboardUtil.show(mContext, editText, 50);
                }
            });
        }

        private void bind(final ToDoCategory toDoCategory) {
            mCategory = toDoCategory;
            mCateView.setColor(mCategory.getIntColor());
            mNameTextView.setText(mCategory.getName());
        }
    }

    public interface Callback {
        void onClickSelectCategory(ToDoCategory category);
    }
}
