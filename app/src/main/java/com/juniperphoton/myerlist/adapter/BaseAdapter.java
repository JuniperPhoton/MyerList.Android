package com.juniperphoton.myerlist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.juniperphoton.myerlist.realm.RealmUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public abstract class BaseAdapter<T, U extends BaseAdapter.BaseViewHolder>
        extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private static final int HEADER = 1;
    private static final int ITEM = 1 << 1;
    private static final int FOOTER = 1 << 2;

    private List<T> mData = new ArrayList<>();

    private View mHeaderView;
    private View mFooterView;

    public BaseAdapter() {
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                if (mHeaderView != null) {
                    return new BaseViewHolder(mHeaderView);
                }
                break;
            case FOOTER:
                if (mFooterView != null) {
                    return new BaseViewHolder(mFooterView);
                }
                break;
        }
        return (U) onCreateItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BaseAdapter.BaseViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM) {
            int index = -1;
            if (mHeaderView != null) {
                index = holder.getAdapterPosition() - 1;
            } else index = holder.getAdapterPosition();
            onBindItemViewHolder((U) holder, index);
        }
    }

    protected abstract U onCreateItemViewHolder(ViewGroup parent);

    protected abstract void onBindItemViewHolder(U holder, int dataPosition);

    public void setHeaderView(View view) {
        mHeaderView = view;
    }

    public void setFooterView(View view) {
        mFooterView = view;
    }

    public List<T> getData() {
        return mData;
    }

    public boolean hasHeader() {
        return mHeaderView != null;
    }

    public boolean hasFooter() {
        return mFooterView != null;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mData != null) count += mData.size();
        if (mHeaderView != null) count++;
        if (mFooterView != null) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null) {
            if (position == 0) return HEADER;
            if (mData != null && position < mData.size() - 1) return ITEM;
        } else {
            if (mData != null && position < mData.size()) return ITEM;
        }
        if (mFooterView != null && position == getItemCount() - 1) {
            return FOOTER;
        }
        return ITEM;
    }

    public void refreshData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public T getData(int dataIndex) {
        return mData.get(dataIndex);
    }

    public void addData(final T item) {
        RealmUtils.getMainInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mData.add(item);
                notifyItemInserted(mData.indexOf(item) + (mHeaderView != null ? 1 : 0));
            }
        });
    }

    public void removeData(final int index) {
        RealmUtils.getMainInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mData.remove(index);
                notifyItemRemoved(mHeaderView != null ? index + 1 : index);
            }
        });
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        View rootView;

        BaseViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
        }
    }
}
