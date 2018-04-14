package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/23/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Sagar on 3/10/2018.
 */

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_AVAILABLE_ACTION = "com.shuvro.barcodescanner";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE, isConectedToInternet(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent);

    }

    private boolean isConectedToInternet(Context context) {
        try {
            if (context != null){
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }return  false;
        }catch (Exception e){
            Log.e(NetworkStateChangeReceiver.class.getName(),e.getMessage());
            return  false;
        }
    }
}

