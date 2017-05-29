package com.juniperphoton.myerlist.presenter

import com.juniperphoton.myerlist.model.ToDoCategory

class CustomCategoryContract {
    interface View {
        fun initData(data: MutableList<ToDoCategory>)

        fun showDialog()

        fun hideDialog(delay: Int)

        fun hideKeyboard()

        fun finish()
    }

    interface Presenter : com.juniperphoton.myerlist.presenter.Presenter {
        fun commit()

        fun refreshData()
    }
}