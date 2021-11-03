package com.farthestgate.suspensions.android.helper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.farthestgate.suspensions.android.base.AppController;
import com.farthestgate.suspensions.android.utils.NetworkUtil;


/**
 * Created by Suraj on 15/09/2016.
 */
public class NetworkStateChangeReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        boolean connected = NetworkUtil.isConnected(context);
        AppController nvApplication = (AppController) context.getApplicationContext();
        if (nvApplication != null) {
            nvApplication.setConnected(connected);
        }
    }
}
