package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/23/2018.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Sagar on 2/16/2018.
 */

public class isConnected {
    Context context;
    boolean connectivity_flag = false;

    public isConnected(Context context) {
        this.context = context;
    }
    public void Connected_to_Internet ()
    {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)
        {
            connectivity_flag =true;
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("No Internet Connection")
                    .setMessage("A connection to the internet is required to log into" +
                            " your Tinker Bell account.")
                    .setCancelable(false)
                    .setPositiveButton("Go to setting ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent callGPSSettingsIntent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity (callGPSSettingsIntent);
                            connectivity_flag =true;
                        }
                    });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    // Toast toast = new Toast(getApplicationContext());
                    Toast.makeText(context,"you must connect to the network to continue",Toast.LENGTH_LONG).show();
                    connectivity_flag =false;
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

    }
}
