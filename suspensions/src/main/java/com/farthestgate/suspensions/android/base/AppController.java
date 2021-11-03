package com.farthestgate.suspensions.android.base;

import android.app.Application;
import android.content.Context;
import android.view.View;

import com.farthestgate.suspensions.android.utils.DialogUtils;
import com.farthestgate.suspensions.android.utils.NetworkUtil;

public class AppController extends Application {

    private static Context context;
    private boolean connected;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        setConnected(NetworkUtil.isConnected(this));
    }

    public static Context getContext() {
        return context;
    }

    public boolean isConnected() {
        return connected;
    }


    public boolean isConnected(boolean showToast) {
        if (!connected) DialogUtils.showNoNetworkToast(this);
        return connected;
    }

    public boolean isConnected(View view) {
        if (!connected) DialogUtils.showNoNetworkSnackBar(view);
        return connected;
    }

    public boolean isConnected(View view, View.OnClickListener retryListener) {
        if (!connected) DialogUtils.showNoNetworkSnackBar(view, retryListener);
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
