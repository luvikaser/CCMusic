package com.cc.presenter;

import android.content.Context;

import com.cc.MusicApplication;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Author: NT
 * Since: 10/26/2016.
 */

public class BaseAppPresenter {
    protected final Context applicationContext = MusicApplication.getInstance();
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();

    public void unSubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }


}
