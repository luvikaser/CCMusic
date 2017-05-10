package com.cc.ui.karaoke.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.cc.ui.karaoke.receiver.notify.VMObservableObject;
import com.cc.ui.karaoke.utils.SystemUtil;

/**
 * Author: NT
 * Since: 9/11/2016.
 */
public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            handleConnectivityChange(context, intent);
        }
    }


    private void handleConnectivityChange(Context context, Intent intent) {
        boolean isHasNetwork = SystemUtil.isNetworkAvailable(context);
        VMObservableObject.getInstance().updateData(isHasNetwork);
    }
}