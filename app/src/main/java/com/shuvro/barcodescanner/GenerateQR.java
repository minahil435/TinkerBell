package com.shuvro.barcodescanner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.Manifest;

public class GenerateQR extends AppCompatActivity
{

        ImageView imageView;
        String keyValue;
       Button button;
        Button button2;
        Thread thread ;
        public final static int QRcodeWidth = 500 ;
        Bitmap bitmap ;
    private FirebaseDatabase FB;
    private DatabaseReference AR;
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.generate_qr);
            imageView = (ImageView)findViewById(R.id.logo);
            button = (Button)findViewById(R.id.sendCode);
            keyValue=getIntent().getStringExtra("keyvalue");

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://play.google.com"+"/TINKERBELL"+keyValue))
                    .setDynamicLinkDomain("bf528.app.goo.gl")
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.google.ios").build())
                    .buildDynamicLink();

            final Uri dynamicLinkUri = dynamicLink.getUri();
            try
            {
                bitmap = TextToImageEncode(dynamicLinkUri.toString());
            } catch (WriterException e)
            {
                e.printStackTrace();
            }


            imageView.setImageBitmap(bitmap);

            MySharedPreference.getInstance(getApplicationContext()).Storedyanmiclink(dynamicLinkUri.toString());



        }


        Bitmap TextToImageEncode(String Value) throws WriterException
        {
            BitMatrix bitMatrix;
            try {
                bitMatrix = new MultiFormatWriter().encode(
                        Value,
                        BarcodeFormat.DATA_MATRIX.QR_CODE,
                        QRcodeWidth, QRcodeWidth, null
                );

            } catch (IllegalArgumentException Illegalargumentexception) {

                return null;
            }
            int bitMatrixWidth = bitMatrix.getWidth();

            int bitMatrixHeight = bitMatrix.getHeight();

            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {

                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            getResources().getColor(R.color.tinkerbell):getResources().getColor(R.color.addColor);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

            bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
            return bitmap;
        }

        public void saving(View v)
        {
            // ActivityCompat.requestPermissions(GenerateQR.this,new String[]{Manifest.WRITE_TO_EXTERNAL_STORAGE},1);
            int rc3= ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (rc3 == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(GenerateQR.this, "Image Saved to gallery", Toast.LENGTH_LONG);
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "DemoQR");
                File dir = new File(path, "");
                dir.mkdirs();
                File file = new File(dir, "QR.JPEG");
                OutputStream out = null;

                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                    //get uri of QR image file
                    Uri uri = Uri.parse("android.resource://this.package.here/drawable/image_name");
                    Toast.makeText(this, "Image Saved to gallery", Toast.LENGTH_SHORT);
                    ContentResolver contentResolver = getApplicationContext().getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getExtensionFromMimeType(contentResolver.getType(uri));

                    ScanFile(getApplicationContext(), file, type);
                    out.flush();
                    out.close();
                    startActivity(new Intent(GenerateQR.this,Profile.class));
                } catch (java.io.IOException e)
                {
                    e.printStackTrace();
                }
            }
            else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        25);
            }}

//            Toast.makeText(GenerateQR.this, "Image Saved to gallery", Toast.LENGTH_LONG);
//            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "DemoQR");
//            File dir = new File(path, "");
//            dir.mkdirs();
//            File file = new File(dir, "QR.JPEG");
//            OutputStream out = null;
//
//            try {
//                out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//
//                //get uri of QR image file
//                Uri uri = Uri.parse("android.resource://this.package.here/drawable/image_name");
//                Toast.makeText(this, "Image Saved to gallery", Toast.LENGTH_SHORT);
//                ContentResolver contentResolver = getApplicationContext().getContentResolver();
//                MimeTypeMap mime = MimeTypeMap.getSingleton();
//                String type = mime.getExtensionFromMimeType(contentResolver.getType(uri));
//
//                ScanFile(getApplicationContext(), file, type);
//                out.flush();
//                out.close();
//                startActivity(new Intent(GenerateQR.this,Profile.class));
//            } catch (java.io.IOException e)
//            {
//                e.printStackTrace();
//            }





    //this method will scan the image file for the frist time get the image to show in the gallery
    public void ScanFile (Context context, File file, String mimeType)
    {
        MediaScannerConnection.scanFile(context, new String[] {file.getPath()},new String[] {mimeType},null);
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

                Toast.makeText(GenerateQR.this, "Image Saved to gallery", Toast.LENGTH_LONG);
                File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "DemoQR");
                File dir = new File(path, "");
                dir.mkdirs();
                File file = new File(dir, "QR.JPEG");
                OutputStream out = null;

                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                    //get uri of QR image file
                    Uri uri = Uri.parse("android.resource://this.package.here/drawable/image_name");
                    Toast.makeText(this, "Image Saved to gallery", Toast.LENGTH_SHORT);
                    ContentResolver contentResolver = getApplicationContext().getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getExtensionFromMimeType(contentResolver.getType(uri));

                    ScanFile(getApplicationContext(), file, type);
                    out.flush();
                    out.close();
                    startActivity(new Intent(GenerateQR.this,Profile.class));
                } catch (java.io.IOException e)
                {
                    e.printStackTrace();
                }


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


    }

