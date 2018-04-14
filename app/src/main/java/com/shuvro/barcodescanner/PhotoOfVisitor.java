package com.shuvro.barcodescanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Map;

import static android.R.attr.value;
import static android.R.id.message;
import static android.os.Build.ID;


/**
 * Created by Admin on 3/19/2018.
 */

public class PhotoOfVisitor extends AppCompatActivity
{
    private FirebaseDatabase FB;
    private DatabaseReference AR;
    ImageView imageView;
    TextView textView;
    Button button;
    Boolean clicked;
    String message;

    @Override
    protected void onNewIntent(Intent intent)
    {
         message =intent.getStringExtra("key");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openthedoor);


    }

    @Override
    protected void onStart()
    {
        super.onStart();
        super.onRestart();
        message =getIntent().getStringExtra("key");
        onNewIntent(getIntent());







        FB = FirebaseDatabase.getInstance();
        AR = FB.getReference().child("visitor");

        imageView = (ImageView) findViewById(R.id.image1);
        textView = (TextView) findViewById(R.id.nameofvisitor);
        button = (Button) findViewById(R.id.open);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {



            }
        });



        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(5)
                .playOn(button);







        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String ID=user.getUid();



        AR.child(ID).child(message).addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                kepingVisitorsRecord recent = dataSnapshot.getValue(kepingVisitorsRecord.class);
                textView.setText(recent.getVisitor());
                Picasso.with(PhotoOfVisitor.this).load(recent.getUri()).fit().centerCrop().into(imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }


        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        finish();

    }
}