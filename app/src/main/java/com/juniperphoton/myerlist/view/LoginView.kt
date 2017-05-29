package com.juniperphoton.myerlist.view

interface LoginView {
    fun navigateToMain(ok: Boolean)

    val email: String

    val password: String

    val secondPassword: String
}