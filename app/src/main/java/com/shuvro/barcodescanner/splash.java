package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/23/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.shuvro.barcodescanner.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;
import static com.shuvro.barcodescanner.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;
import android.Manifest;



public class splash extends AppCompatActivity
{

    //Connectivity variables
    getGPSInfo gpsInfo = new getGPSInfo(this);
    isConnected connected = new isConnected(this);
    boolean internet_connectivity_flag = false;
    boolean gps_connectivity_flag = false;
    boolean callback=false;

    private FirebaseDatabase FB;
    private DatabaseReference AR;

    TextView textView;
    ImageView imageView;

    private int requeststorage=15;
    private int requestlocation=25;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        textView=(TextView) findViewById(R.id.logotext);
        imageView=(ImageView) findViewById(R.id.logo);
        Animation myanimation= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        textView.startAnimation(myanimation);
        imageView.startAnimation(myanimation);

        int rc = ActivityCompat.checkSelfPermission(splash.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int rc2 = ActivityCompat.checkSelfPermission(splash.this, Manifest.permission.ACCESS_FINE_LOCATION);


        if (rc == PackageManager.PERMISSION_GRANTED&&rc2 == PackageManager.PERMISSION_GRANTED)
        {
            splashFlow();

        }
        else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    25);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    15);



//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                    Uri.fromParts("package", getPackageName(), null));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 25)
        {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

                splashFlow();


                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else
                {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            return;
        }
    }

            // other 'case' lines to check for other
            // permissions this app might request.



    @Override
    protected void onRestart() {
        super.onRestart();
        splashFlow();
    }

    public void splashFlow()
    {
        checkConnection();
        if (internet_connectivity_flag == true && gps_connectivity_flag == true)
        {
            goToMainScreen();
        } else
            {

            showSnack();
        }
        checkConnection();
    }

    public void showSnack()
    {

        View snackVeiw = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(snackVeiw, "No Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gpsInfo.GPSCheck();
                        connected.Connected_to_Internet();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void checkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED) {
            internet_connectivity_flag = true;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        Location location;
        Criteria criteria = new Criteria();
        String provider;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
            gps_connectivity_flag =true;

        }
    }

    public void goToMainScreen()
    {


        FB = FirebaseDatabase.getInstance();
        AR = FB.getReference().child("user");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {

            final String ID=user.getUid();
            AR.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot snapshot)
                {
                    if (snapshot.hasChild(ID))
                    {
                        Intent i = new Intent(splash.this, Profile.class);
                        startActivity(i);
                    } else
                        {
                        Intent i = new Intent(splash.this, UserAddition.class);
                        startActivity(i);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });


        } else
            {
            startActivity(new Intent(splash.this, NumberVerification.class));
                finish();
        }

        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        final View v = findViewById(android.R.id.content);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE,false);
                String networkStatus = isNetworkAvailable ? "connected" :"disconnected";
                Snackbar.make(v,"Network Status:"+networkStatus,Snackbar.LENGTH_LONG).show();

            }
        },intentFilter);
    }
}

