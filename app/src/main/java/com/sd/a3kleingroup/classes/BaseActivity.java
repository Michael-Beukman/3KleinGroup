package com.sd.a3kleingroup.classes;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.LoginActivity;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.d("LOG_MyBaseActivity1", "re");

        // check if logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            goToLogin();
            return;
        }

        MyFirebaseMessagingService x = new MyFirebaseMessagingService();
        Log.d("LOG_MyBaseActivity", String.valueOf(x));
        x.getToken();
    }

    public void goToLogin(){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }


}
