package com.juniperphoton.myerlistandroid.presenter;

import com.juniperphoton.myerlistandroid.model.ToDo;

public class MainContract {
    public interface View {
        void displayCategories();

        void displayToDos();

        void uploadOrders();

        void notifyToDoDeleted(int pos);
    }

    public interface Presenter extends com.juniperphoton.myerlistandroid.presenter.Presenter {
        void getCates();

        void getToDos();

        void updateOrders(String order);

        void updateIsDone(ToDo toDo);

        void modifyToDo(final String cate, final String content, final String id);

        void addToDo(String cate, String content);

        void deleteToDo(ToDo toDo);

        void clearDeletedList();

        void recoverToDo(ToDo toDo);
    }
}
