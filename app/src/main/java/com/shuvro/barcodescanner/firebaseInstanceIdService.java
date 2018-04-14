package com.shuvro.barcodescanner;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Admin on 2/1/2018.
 */

public class firebaseInstanceIdService extends FirebaseInstanceIdService

{

    public static final String TOKEN="TOKENDEMO";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       // getApplication().sendBroadcast(new Intent(TOKEN));
        storeToken(refreshedToken);
    }
    private void storeToken(String token)
    {
        MySharedPreference.getInstance(getApplicationContext()).StoreToken(token);

    }




}

