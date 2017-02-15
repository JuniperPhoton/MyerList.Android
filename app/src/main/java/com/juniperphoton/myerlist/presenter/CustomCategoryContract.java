package com.juniperphoton.myerlist.presenter;


import com.juniperphoton.myerlist.model.ToDoCategory;

import java.util.List;

public class CustomCategoryContract {
    public interface View {
        void initData(List<ToDoCategory> data);

        void showDialog();

        void hideDialog(int delay);

        void hideKeyboard();

        void finish();
    }

    public interface Presenter extends com.juniperphoton.myerlist.presenter.Presenter {
        void cancel();

        void commit();

        void refreshData();
    }
}
