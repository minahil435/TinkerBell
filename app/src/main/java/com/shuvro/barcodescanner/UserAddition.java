package com.shuvro.barcodescanner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

import static android.R.attr.id;
import static com.shuvro.barcodescanner.R.id.address;


public class UserAddition extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private FirebaseDatabase FB;
    private DatabaseReference AR;
    protected String a;
    protected String b;
    protected static final String TAG = "Location Lesson 2-1";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected TextView mLongitudeText;
    protected LocationRequest mLocationRequest;
    private AlertDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useraddition);
        buildGoogleApiClient();
    }


    public void click(View view)
    {


        FB = FirebaseDatabase.getInstance();
        AR = FB.getReference().child("user");



        EditText name = (EditText) findViewById(R.id.username);
        EditText address = (EditText) findViewById(R.id.address);
        CheckBox checkBox=(CheckBox)findViewById(R.id.checkBox);
        if (name.getText() != null && address.getText()!=null &&checkBox.isChecked() )
        {

            progressDialog = new SpotsDialog(this, R.style.Custom3);
            progressDialog.show();
            String token = MySharedPreference.getInstance(getApplicationContext()).GetToken();

            user newUser = new user(name.getText().toString(), address.getText().toString(), a, b, "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\neSuD/x/CqjmGG0vstgAAAABJRU5ErkJggg==\n");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String ID = user.getUid();
            AR.child(ID).setValue(newUser);



            Intent intend = new Intent(this, GenerateQR.class);
            progressDialog.dismiss();
            intend.putExtra("keyvalue", ID);
            startActivity(intend);
            finish();

        }

        else
            {
                Toast.makeText(this,"incomplete userdetails", Toast.LENGTH_SHORT).show();
        }
    }


    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint)
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, location.toString());
        Double aa= location.getLatitude();
        Double bb = location.getLongitude();
        if (aa==0.00&&bb==0.00)
        {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);

        }
        else {
            a = aa.toString();
            b = bb.toString();
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());

    }

    /*
    * Called by Google Play services if the connection to GoogleApiClient drops because of an
    * error.
    */
    public void onDisconnected() {
        Log.d(TAG, "Disconnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
public void asGuest(View v)
{
    startActivity(new Intent(UserAddition.this, BarcodescannerFORGUEST.class));
}


}
