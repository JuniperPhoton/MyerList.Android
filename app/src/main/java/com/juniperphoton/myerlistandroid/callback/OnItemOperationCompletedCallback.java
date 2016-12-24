package com.juniperphoton.myerlistandroid.callback;


import android.view.View;

public interface OnItemOperationCompletedCallback {
    void onArrangeCompleted();

    void onUpdateDone(int position);

    void onDelete(int position);

    void onClickedItem(int position, View cateView);
}
