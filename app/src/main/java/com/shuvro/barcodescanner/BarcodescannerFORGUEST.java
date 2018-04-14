package com.shuvro.barcodescanner;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;

import dmax.dialog.SpotsDialog;

import static android.R.attr.duration;
import static android.os.Build.ID;

public class BarcodescannerFORGUEST extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String firebaseUser;
    private static final int PLANT_DETAIL_LOADER = 2;
    private static final String INTENT_EXTRA_ITEM = "item_id";
    String scanContent;
    String scanFormat;
    TextView textView;
    Button button;
    private FirebaseDatabase FB;
    private DatabaseReference AR;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;
    protected static final String TAG = "Location Lesson 2-1";
    protected double calculatedLatitude;
    protected double calculatedLongitude;
    protected double storedLatitude;
    protected double storedLongitude;
    double difference;
    public Location L;
    String userName;
    Uri deepLink;
    Boolean haschild;
    private AlertDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        buildGoogleApiClient();

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.sendCode);
        haschild = false;


        IntentIntegrator scanIntegrator = new IntentIntegrator(BarcodescannerFORGUEST.this);
        scanIntegrator.setPrompt("Scan");
        scanIntegrator.setBeepEnabled(true);

        //enable the following line if you want QR code
        //scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null)
            {

                progressDialog = new SpotsDialog(this, R.style.Custom);
                progressDialog.show();
                scanContent = scanningResult.getContents().toString();
                if (scanContent.contains("https://bf528.app.goo.gl")) {
                    String[] split = scanContent.split("TINKERBELL");
                    scanContent = split[1];
                    matchVisitor();
                } else {
                    Toast.makeText(this, "This QR code doesnt belong to TinkerBell", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }

            }


        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
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
    public void onLocationChanged(Location location) {

        calculatedLatitude = location.getLatitude();
        calculatedLongitude = location.getLongitude();


    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
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


    private boolean differenceInPoints() {
        if (difference < 200) {
            return true;
        } else
            return false;
    }


    void matchVisitor()
    {


        FB = FirebaseDatabase.getInstance();
        AR = FB.getReference().child("user");
        AR.orderByKey().addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                if (snapshot.hasChild(scanContent))
                {

                    AR.child(scanContent).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            user household = dataSnapshot.getValue(user.class);
                            storedLatitude = Double.parseDouble(household.getLatitude().toString());
                            storedLongitude = Double.parseDouble(household.getLongitude().toString());
                            float[] results = new float[3];
                            Location.distanceBetween(storedLatitude, storedLongitude, calculatedLatitude, calculatedLongitude, results);
                            difference = results[0];
                            if (differenceInPoints())
                            {progressDialog.dismiss();

                                Intent intend = new Intent(BarcodescannerFORGUEST.this, FaceTrackerActivityFORGUEST.class);
                                intend.putExtra("scanContent", scanContent);
                                startActivity(intend);
                                finish();
                            }
                            else {
                                progressDialog.dismiss();

                                Toast.makeText(BarcodescannerFORGUEST.this, "Your location don't match ", Toast.LENGTH_SHORT).show();

                                Intent intend = new Intent(BarcodescannerFORGUEST.this, UserAddition.class);
                                startActivity(intend);
                                finish();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                } else
                    {
                    progressDialog.dismiss();
                    Toast.makeText(BarcodescannerFORGUEST.this, "This QR code dont belongs to any Tinkerbell user", Toast.LENGTH_SHORT).show();
                    Intent intend = new Intent(BarcodescannerFORGUEST.this, UserAddition.class);
                    startActivity(intend);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}