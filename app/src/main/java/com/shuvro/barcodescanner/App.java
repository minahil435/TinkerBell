package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/23/2018.
 */

import android.app.Application;
import android.content.IntentFilter;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


public class App extends Application {
    private static final String WIFI_STATE_CHANGE_ACTION = "android.net.WIFI_STAT_CHANGED";

    @Override
    public void onCreate() {
        super.onCreate();
        registerForNetworkChangeEvents(this);
    }

    private void registerForNetworkChangeEvents(App app) {
        NetworkStateChangeReceiver networkStateChangeReceiver = new NetworkStateChangeReceiver();
        app.registerReceiver(networkStateChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        app.registerReceiver(networkStateChangeReceiver, new IntentFilter(WIFI_STATE_CHANGE_ACTION));
    }
}
