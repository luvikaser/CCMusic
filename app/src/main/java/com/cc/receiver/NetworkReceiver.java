package com.cc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cc.MusicApplication;
import com.cc.event.NetworkChangeEvent;
import com.cc.domain.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * Author: NT
 * Since: 10/27/2016.
 */
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOnline = NetworkUtils.isNetworkAvailable(context);

        EventBus eventBus = MusicApplication.getInstance().getAppComponent().eventBus();
        eventBus.post(new NetworkChangeEvent(isOnline));
    }
}
