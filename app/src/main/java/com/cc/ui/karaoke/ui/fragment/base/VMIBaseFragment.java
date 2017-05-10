package com.cc.ui.karaoke.ui.fragment.base;



public interface VMIBaseFragment {
    void showProgressDialog();

    void hideProgressDialog();

    void showNetworkError();

    void showGeneralErrorDialog();

    void showErrorDialog(String message, String statusDesc);

    void showDialog(String title, String description);
 }
