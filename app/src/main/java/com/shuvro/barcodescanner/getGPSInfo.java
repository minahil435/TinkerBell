package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/23/2018.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;


public class getGPSInfo {
    Context context;
    public boolean connectivityFlag = false;

    public getGPSInfo(Context context) {
        this.context = context;
    }

    public void GPSCheck() {
        Criteria criteria = new Criteria();
        String provider;
        Location location;
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);
            connectivityFlag =true;

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("GPS Disabled").setMessage("A GPS location is required to log into " +
                    "your Tinker Bell account.")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent callGPSSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(callGPSSettingsIntent);
                            connectivityFlag =true;
                        }
                    });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Toast toast = new Toast(context);
                    toast.makeText(context, "you must enable your GPS to continue", Toast.LENGTH_LONG).show();
                    connectivityFlag =false;
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

        }

    }
}

