package com.juniperphoton.myerlistandroid.view;


public interface LoginView {
    void afterLogin(boolean ok);
    String getEmail();
    String getPassword();
    String getSecondPassword();
}
