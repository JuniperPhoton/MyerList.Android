package com.juniperphoton.myerlist.presenter;

import com.juniperphoton.myerlist.model.ToDo;

public class MainContract {
    public interface View {
        void displayCategories();

        void displayToDos();

        void uploadOrders();

        void notifyToDoDeleted(int pos);
    }

    public interface Presenter extends com.juniperphoton.myerlist.presenter.Presenter {
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
