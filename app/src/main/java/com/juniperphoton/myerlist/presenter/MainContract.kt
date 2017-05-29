package com.juniperphoton.myerlist.presenter

import com.juniperphoton.myerlist.model.ToDo

class MainContract {
    interface View {
        fun refreshCategoryList()

        fun refreshToDoList()

        fun notifyDataSetChanged()

        fun uploadOrders()

        fun toggleRefreshing(show: Boolean)

        fun notifyToDoDeleted(pos: Int)
    }

    interface Presenter : com.juniperphoton.myerlist.presenter.Presenter {
        fun getCategories()

        fun getToDos()

        fun updateOrders(order: String)

        fun updateIsDone(toDo: ToDo)

        fun modifyToDo(cate: String, content: String, id: String)

        fun addToDo(cate: String, content: String)

        fun deleteToDo(toDo: ToDo)

        fun clearDeletedList()

        fun recoverToDo(toDo: ToDo)
    }
}
