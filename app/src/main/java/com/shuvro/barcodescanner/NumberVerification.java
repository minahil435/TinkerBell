package com.shuvro.barcodescanner;

/**
 * Created by Admin on 3/20/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Admin on 1/19/2018.
 */

public class NumberVerification extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth FA;
    private String phoneVerificationID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallsback;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseDatabase FB;
    private DatabaseReference AR;


    EditText password;
    Button go;
    Button resend;
    Button verifycode;
    public FirebaseUser firebaseUser;
    EditText phoneNumberEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_verification);



        final Button send = (Button) findViewById(R.id.sendphonenumber);
        send.setOnClickListener((View.OnClickListener) this);

        phoneNumberEditText = (EditText) findViewById(R.id.phone);


        FA = FirebaseAuth.getInstance();
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==11)
                {
                    send.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });


    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.sendphonenumber) {
            String phoneNumber = phoneNumberEditText.getText().toString();

            if (phoneNumber.matches("") || phoneNumber.length() != 11 || !phoneNumber.matches("[0-9]+")) {
                Toast.makeText(this, "please enter a valid phonenumber to proceed ", Toast.LENGTH_SHORT).show();
                return;
            } else {
                phoneNumber = phoneNumber.substring(1);
                phoneNumber = "+92" + phoneNumber;
                Intent i = new Intent(NumberVerification.this, CodeVerification.class);
                i.putExtra("number", phoneNumber);
                startActivity(i);
                finish();

            }

        }

    }


}