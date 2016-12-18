package com.juniperphoton.myerlistandroid.callback;


public interface OnItemOperationCompletedCallback {
    void onArrangeCompleted();

    void onUpdateDone(int position);

    void onDelete(int position);
}
