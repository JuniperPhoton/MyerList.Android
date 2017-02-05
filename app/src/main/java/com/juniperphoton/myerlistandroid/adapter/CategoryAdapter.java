package com.juniperphoton.myerlistandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.activity.CategoryManagementActivity;
import com.juniperphoton.myerlistandroid.activity.MainActivity;
import com.juniperphoton.myerlistandroid.callback.OnDrawerSelectedChanged;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.widget.CircleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter extends BaseAdapter<ToDoCategory, CategoryAdapter.CategoryViewHolder> {

    private OnDrawerSelectedChanged mCallback;
    private int mSelectedIndex = -1;
    private Context mContext;

    public CategoryAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected CategoryViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(App.getInstance()).inflate(R.layout.row_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(final CategoryViewHolder holder, final int dataPosition) {
        holder.bind(getData(dataPosition));
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataPosition == mSelectedIndex) return;
                selectItem(dataPosition);
            }
        });
    }

    public void selectItem(final int position) {
        final ToDoCategory category = getData(position);
        if (category.getId() != ToDoCategory.PERSONALIZATION_ID) {
            int lastIndex = mSelectedIndex;
            mSelectedIndex = position;
            notifyItemChanged(lastIndex);
            notifyItemChanged(position);
        }
        if (mCallback != null) {
            mCallback.onSelectedChanged(category, position);
        }
    }

    private ToDoCategory getItemById(String cateId) {
        for (int i = 0; i < getData().size(); i++) {
            ToDoCategory category = getData(i);
            if (category.getId() == Integer.valueOf(cateId)) {
                return category;
            }
        }
        return null;
    }

    public int getItemIndexById(String cateId) {
        ToDoCategory toDoCategory = getItemById(cateId);
        if (toDoCategory != null) {
            return getData().indexOf(toDoCategory);
        }
        return -1;
    }

    public void setSelectedCallback(OnDrawerSelectedChanged callback) {
        mCallback = callback;
    }

    class CategoryViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.row_cate_color_view)
        CircleView circleView;

        @BindView(R.id.row_cate_name)
        TextView nameView;

        @BindView(R.id.row_cate_back)
        View mBackView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(ToDoCategory category) {
            circleView.setColor(Color.parseColor(category.getColor()));
            nameView.setText(category.getName());
            if (getAdapterPosition() == mSelectedIndex) {
                setSelected();
            } else {
                setUnselected();
            }
        }

        void setSelected() {
            mBackView.setVisibility(View.VISIBLE);
        }

        void setUnselected() {
            mBackView.setVisibility(View.GONE);
        }
    }
}
