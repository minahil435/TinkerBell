package com.shuvro.barcodescanner;

        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.FirebaseException;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.PhoneAuthCredential;
        import com.google.firebase.auth.PhoneAuthProvider;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import java.util.concurrent.TimeUnit;

        import dmax.dialog.SpotsDialog;

        import static com.shuvro.barcodescanner.R.drawable.send;
        import static com.shuvro.barcodescanner.R.id.box2;
        import static com.shuvro.barcodescanner.R.id.box3;
        import static com.shuvro.barcodescanner.R.id.box4;
        import static com.shuvro.barcodescanner.R.id.box5;
        import static com.shuvro.barcodescanner.R.id.box6;
        import static com.shuvro.barcodescanner.R.id.sendcode;

public class CodeVerification extends AppCompatActivity implements View.OnClickListener
{



    private FirebaseAuth FA;
    private String phoneVerificationID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallsback;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseDatabase FB;
    private DatabaseReference AR;


    EditText phoneNumberEditText ;
    EditText password ;
    Button go ;
    Button resend;
    Button verifycode;
    public FirebaseUser firebaseUser;

    EditText box1;
    EditText box2;
    EditText box3;
    EditText box4;
    EditText box5;
    EditText box6;
    Button sendCode;
    private AlertDialog progressDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_verification);
        String phoneNumber=getIntent().getStringExtra("number");

        SetUpVerificationCallBacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 120, TimeUnit.SECONDS, this, verificationCallsback);


        sendCode=(Button) findViewById(sendcode);
        sendCode.setOnClickListener((View.OnClickListener) this);
        box1 = (EditText) findViewById(R.id.box1);
        box2 = (EditText) findViewById(R.id.box2);
        box3 = (EditText) findViewById(R.id.box3);
        box4 = (EditText) findViewById(R.id.box4);
        box5 = (EditText) findViewById(R.id.box5);
        box6 = (EditText) findViewById(R.id.box6);
        box1.addTextChangedListener(new TextWatcher()
        {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==1)
                {
                    box2.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });
        box2.addTextChangedListener(new TextWatcher()
        {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==1)
                {
                    box3.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });

        box3.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==1)
                {
                    box4.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });
        box4.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==1)
                {
                    box5.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });
        box5.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==1)
                {
                    box6.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });
        box6.addTextChangedListener(new TextWatcher()
        {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()==1)
                {
                    sendCode.requestFocus();
                }

            }

            public void afterTextChanged(Editable s)
            {
            }
        });



        FA = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view)
    {

        if (view.getId() == sendcode) {

            String code = box1.getText().toString();
            String code2 = box2.getText().toString();
            String code3 = box3.getText().toString();
            String code4 = box4.getText().toString();
            String code5 = box5.getText().toString();
            String code6 = box6.getText().toString();


            if (code.matches("") || code2.matches("") || code3.matches("") || code4.matches("") || code5.matches("") || code6.matches("")) {
                Toast.makeText(this, "Incomplete code ", Toast.LENGTH_SHORT).show();
                return;
            } else
                {
                    String phonecode=code+code2+code3+code4+code5+code6;
                    progressDialog = new SpotsDialog(CodeVerification.this, R.style.Custom2);
                    progressDialog.show();



                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(phoneVerificationID, phonecode);
                signInwithAuthCredential(phoneAuthCredential);

            }
        }

    }

    private void SetUpVerificationCallBacks()
    {
        verificationCallsback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInwithAuthCredential(phoneAuthCredential);

            }


            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "network failure", Toast.LENGTH_LONG).show();
                Intent i =new Intent(CodeVerification.this,Profile.class);
                startActivity(i);
                finish();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                phoneVerificationID = s;
                resendingToken = forceResendingToken;
            }
        };


    }

    void signInwithAuthCredential(PhoneAuthCredential phoneAuthCredential)
    {
        FA.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                if (task.isSuccessful())
                {
                    firebaseUser=task.getResult().getUser();

                    FB = FirebaseDatabase.getInstance();
                    AR = FB.getReference().child("Token");

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String ID=user.getUid();
                    String token=MySharedPreference.getInstance(getApplicationContext()).GetToken();
                    AR.child(ID).setValue(token);

                    AR = FB.getReference().child("user");
                    AR.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot snapshot)
                        {
                            if (snapshot.hasChild(ID))
                            {

                                Intent i =new Intent(CodeVerification.this,Profile.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {

                                Intent i =new Intent(CodeVerification.this,UserAddition.class);
                                startActivity(i);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });




                    ;
                }
                else
                {

                }


            }
        });
    }

}


