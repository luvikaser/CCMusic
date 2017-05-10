package com.cc.presenter;

/**
 * Author: NT
 * Since: 10/26/2016.
 */
public interface IPresenter<View> {

    void setView(View view);

    void destroyView();

    void resume();

    void pause();

    void destroy();
}
