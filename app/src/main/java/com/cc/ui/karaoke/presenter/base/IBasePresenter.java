package com.cc.ui.karaoke.presenter.base;

/**
 * Author: NT
 * Email: duynguyen.developer@yahoo.com
 */
public interface IBasePresenter<ViewType> {
    void setView(ViewType view);

    void destroyView();
}
