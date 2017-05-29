package com.juniperphoton.myerlist.util

fun String.isEmailFormat(): Boolean = java.util.regex.Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", this)