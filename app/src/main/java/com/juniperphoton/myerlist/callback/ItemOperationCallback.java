package com.juniperphoton.myerlist.callback;


import android.view.View;

public interface ItemOperationCallback {
    void onArrangeCompleted();

    void onUpdateDone(int position);

    void onDelete(int position);

    void onClickedItem(int position, View cateView);

    void onClickRecover(int position);
}
