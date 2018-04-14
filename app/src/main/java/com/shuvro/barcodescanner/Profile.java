package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/20/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import dmax.dialog.SpotsDialog;

import static android.R.attr.bitmap;
import static android.R.attr.id;
import static android.R.id.message;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.shuvro.barcodescanner.R.id.checkBox;
import static com.shuvro.barcodescanner.R.id.profile_image;
import static com.shuvro.barcodescanner.R.layout.profile;
import static java.lang.Boolean.TRUE;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    Button scanner;
    Button guestlist;
    TextView signout;

    ImageView profilePicture;
    TextView address;
    TextView Name;

    private FirebaseDatabase FB;
    private DatabaseReference AR;

    String currentuser;
    String picture;
    private AlertDialog progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        ActionBar myActionBar = getSupportActionBar();
        myActionBar.show();


        scanner = (Button) findViewById(R.id.read);
        scanner.setOnClickListener(this);


        guestlist = (Button) findViewById(R.id.guestlist);
        guestlist.setOnClickListener(this);

        signout = (TextView) findViewById(R.id.signout);
        signout.setOnClickListener(this);


        address = (TextView) findViewById(R.id.address);
        Name = (TextView) findViewById(R.id.textView);


        profilePicture = (ImageView) findViewById(R.id.profile_image);
        profilePicture.setOnClickListener(this);
        FB = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AR = FB.getReference().child("user");
        currentuser = user.getUid().toString();

        AR.child(currentuser).addValueEventListener(new ValueEventListener()
        {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            user user1 = dataSnapshot.getValue(user.class);
                            address.setText(user1.getaddress());
                            Name.setText(user1.getname());
                            picture = user1.getprofilepicture();
                            profilePicture.setImageBitmap(StringToBitMap(picture));


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }

                    });

    }
    public Bitmap StringToBitMap(String encodedString)
    {
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.read) {
            startActivity(new Intent(Profile.this, BarcodeScannerActivity.class));

        }
        if (view.getId() == R.id. guestlist) {
            startActivity(new Intent(Profile.this, Listview.class));

        }

        if (view.getId() == R.id.signout)
        {
            progressDialog = new SpotsDialog(this, R.style.Custom4);
            progressDialog.show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            AR = FB.getReference().child("Token");
            currentuser= user.getUid().toString();
            AR.child(currentuser).removeValue();

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {

                        public void onComplete(@NonNull Task<Void> task)

                        {   progressDialog.dismiss();
                            startActivity(new Intent(Profile.this, NumberVerification.class));
                            finish();

                        }
                    });

        }

        if (view.getId() == profile_image)
        {

            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);

        }

}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode == Activity.RESULT_OK)
        {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profilePicture.setImageBitmap(bitmap);

                AR = FB.getReference().child("user");
                AR.child(currentuser).child("profilepicture").setValue(BitMapToString(bitmap));


            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        CreateMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        MenuChoice(item);
        return TRUE;

    }

    private  void CreateMenu(Menu menu)
    {
        menu.add(0,1,1,"Edit Name");
        menu.add(0,2,2,"Edit Address");
        menu.add(0,3,3,"View QR");
        menu.add(0,4,4,"Share");
    }
    private Boolean MenuChoice(MenuItem mt)
    {
        int click =mt.getItemId();
        if (click==1)
        {
            final Dialog dialog = new Dialog(Profile.this);
            dialog.setTitle("Edit Profile");
            dialog.setContentView(R.layout.editing_profile_dialogue);

            TextView text = (TextView) dialog.findViewById(R.id.addition);
            text.setText("Name");

            Button button = (Button) dialog.findViewById(R.id.button);


            final EditText dialogButton = (EditText) dialog.findViewById(R.id.updating);

                    button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            String string=dialogButton.getText().toString();
                            if (!string.isEmpty() )
                            {
                                AR = FB.getReference().child("user").child(currentuser).child("name");
                                AR.setValue(string);
                                dialog.dismiss();
                            }
                                else
                                {
                                    Toast.makeText(Profile.this,"incomplete user details", Toast.LENGTH_SHORT).show();
                                }

                        }


                });

            dialog.show();



        }
        if (click==2)
        {

            final Dialog dialog = new Dialog(Profile.this);
            dialog.setTitle("Edit Profile");
            dialog.setContentView(R.layout.editing_profile_dialogue);

            TextView text = (TextView) dialog.findViewById(R.id.addition);
            text.setText("Address");

            Button button = (Button) dialog.findViewById(R.id.button);

            final EditText dialogButton = (EditText) dialog.findViewById(R.id.updating);

            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    String string = dialogButton.getText().toString();
                    if (!string.isEmpty())
                    {
                        AR = FB.getReference().child("user").child(currentuser).child("address");
                        AR.setValue(string);
                        dialog.dismiss();
                    }
                    else
                    { Toast.makeText(Profile.this,"incomplete user details", Toast.LENGTH_SHORT).show();}

                }

            });

            dialog.show();





        }

        if(click==3)
        {
            startActivity(new Intent(Profile.this,userQR.class));
        }

        if(click==4)
        {
            String message = MySharedPreference.getInstance(getApplicationContext()).Getdynamiclink();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(share, "Share TinkerBell via"));


        }


        return  TRUE;
    }

    @Override
    public void onBackPressed()
    {

    }
}
