
package com.cc.presenter;

import android.content.Context;

/**
 * Author: NT
 * Since: 10/26/2016.
 */
public interface ILoadDataView {

    void showLoading();

    void hideLoading();

    void showRetry();

    void hideRetry();

    void showError(String message);

    Context getContext();

    void showMessage(String message);
}
