package com.juniperphoton.myerlist.view;


public interface LoginView {
    void navigateToMain(boolean ok);

    String getEmail();

    String getPassword();

    String getSecondPassword();
}
