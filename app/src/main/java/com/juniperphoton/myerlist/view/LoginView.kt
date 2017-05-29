package com.juniperphoton.myerlist.view

interface LoginView {
    fun navigateToMain()

    fun dismissDialog()

    val email: String

    val password: String

    val secondPassword: String
}