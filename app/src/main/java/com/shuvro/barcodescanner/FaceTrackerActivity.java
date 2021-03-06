package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/19/2018.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.location.Location;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shuvro.barcodescanner.ui.camera.CameraSourcePreview;
import com.shuvro.barcodescanner.ui.camera.GraphicOverlay;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static android.os.Build.ID;
import static android.widget.Toast.makeText;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity
{
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest captureRequest;
    private ImageReader imageReader;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    Bitmap bitmap;
    private StorageReference mStorageRef;

    Uri myUri;
    private FirebaseDatabase FB;
    private DatabaseReference AR;
    String userName;
    Uri downloadUrl;
    String tokenOfthedevice;
    String Keyofvisitor;
    private AlertDialog progressDialog ;


    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.cameramain);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        FB = FirebaseDatabase.getInstance();

        AR = FB.getReference().child("visitor");
        String visitorID = getIntent().getStringExtra("scanContent");
        Keyofvisitor=  AR.child(visitorID).push().getKey();

        AR = FB.getReference().child("Token");

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();

        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ACCURATE_MODE).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();


        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);


        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            Float lefteye= face.getIsLeftEyeOpenProbability();
            Float righteye=face.getIsRightEyeOpenProbability();

            if(lefteye>=0.5 && righteye>=0.5)
            {

                mCameraSource.takePicture(shutterCallback, jpegCallback);
            }

        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback()
    {
        public void onShutter()
        {
            Toast.makeText(FaceTrackerActivity.this, "picture captured", Toast.LENGTH_SHORT).show();

            //			 Log.d(TAG, "onShutter'd");
        }
    };


    CameraSource.PictureCallback jpegCallback = new CameraSource.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] bytes)
        {
            mPreview.stop();
            
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            final String visitorID = getIntent().getStringExtra("scanContent");
            progressDialog = new SpotsDialog(FaceTrackerActivity.this, R.style.Custom5);
            progressDialog.show();




            AR.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(visitorID))
                    {
                        AR.child(visitorID).addValueEventListener(new ValueEventListener() {


                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                tokenOfthedevice = dataSnapshot.getValue().toString();
                                storePicture();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });

                    } else {
                        Toast.makeText(FaceTrackerActivity.this, "User is not registered to any device", Toast.LENGTH_SHORT).show();
                        Intent intend = new Intent(FaceTrackerActivity.this, Profile.class);
                        startActivity(intend);
                        finish();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    };
        public void storePicture()

        {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference SR = mStorageRef.child("images" + UUID.randomUUID().toString());

        UploadTask uploadTask = SR.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
@Override
public void onFailure(@NonNull Exception exception)
{
        // Handle unsuccessful uploads
        }
        }
        ).addOnSuccessListener
        (new OnSuccessListener<UploadTask.TaskSnapshot>()

        {
@Override
public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
{
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
        downloadUrl = taskSnapshot.getDownloadUrl();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AR = FB.getReference().child("user");
        String w = user.getUid().toString();
        AR.child(w).child("name").addValueEventListener(new ValueEventListener()
        {

@Override
public void onDataChange(DataSnapshot dataSnapshot)
        {
        userName = dataSnapshot.getValue().toString();
        AR = FB.getReference().child("visitor");
        kepingVisitorsRecord kp = new kepingVisitorsRecord(userName, DateFormat.getDateTimeInstance().format(new Date()), downloadUrl.toString());
        String visitorID = getIntent().getStringExtra("scanContent");
        AR.child(visitorID).child(Keyofvisitor).setValue(kp);
            new notifyingOperation().execute(tokenOfthedevice);



        }

@Override
public void onCancelled(DatabaseError databaseError)
{

}

        });



        }


        });


        }

public void sendPushNotification(String token) throws IOException, JSONException
        {
        URL url = new URL("https://fcm.googleapis.com/fcm/send");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "key=" + "AIzaSyClzGjqnuP0fz9q_T2uy4tym1_wIoWj_dU");

        con.setDoOutput(true);

        JSONObject notification = new JSONObject();
        JSONObject manJson = new JSONObject();
        JSONObject message=new JSONObject();

        manJson.put("body", "someone at your door,"+Keyofvisitor);
        manJson.put("title", "Tinker Bell");
        manJson.put("sound","default");
        manJson.put("click_action","activity_1");

            message.put("to",token);
            message.put("data",manJson);





        con.connect();

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(message.toString());
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();


    if(responseCode==200)
    {
        Intent intend = new Intent(FaceTrackerActivity.this, Confirmation.class);
        startActivity(intend);
        finish();

    }
    else
    {
        Intent intend = new Intent(FaceTrackerActivity.this, BarcodeScannerActivity.class);
        startActivity(intend);
        finish();

    }





        }

private class notifyingOperation extends AsyncTask<String , Void, String>
{
    @Override
    protected String doInBackground(String... params)
    {


        try {
            sendPushNotification(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {

        progressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}


}










