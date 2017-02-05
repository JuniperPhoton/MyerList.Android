package com.juniperphoton.myerlist.callback;

import com.juniperphoton.myerlist.model.ToDoCategory;

public interface OnDrawerSelectedChanged {
    void onSelectedChanged(ToDoCategory category, int position);
}
