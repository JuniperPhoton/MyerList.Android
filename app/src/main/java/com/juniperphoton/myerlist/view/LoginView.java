package com.juniperphoton.myerlist.view;


public interface LoginView {
    void afterLogin(boolean ok);

    String getEmail();

    String getPassword();

    String getSecondPassword();
}
