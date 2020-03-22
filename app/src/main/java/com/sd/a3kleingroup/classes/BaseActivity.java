package com.sd.a3kleingroup.classes;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.LoginActivity;
import com.sd.a3kleingroup.MySentFiles;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.SendFileActivity;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;

public abstract class BaseActivity extends AppCompatActivity {
//    @Override
    protected BottomNavigationView nav;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        doNavigation();
    }

    public void goToLogin(){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    protected void doNavigation(){
        nav = findViewById(R.id.bottom_navigation);
        Log.d("LOG_MyBaseActivity1", "NAV IS " + nav);
        if (nav != null){
            nav.setOnNavigationItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        goToHome();
                        return true;
                    case R.id.navigation_mysentfiles:
                        goToMySentFiles();
                        return true;
                    case R.id.navigation_recvFiles:
                        goToRecvFiles();
                        return true;
                    case R.id.navigation_sendfiles:
                        goToSendFiles();
                        return true;
                    case R.id.navigation_message:
                        goToMessages();
                        return true;
                }
                return false;
            });
        }
    }

    private void goToMessages() {
        // todo
    }

    private void goToSendFiles() {
        Intent I = new Intent(getApplicationContext(), SendFileActivity.class);
        startActivity(I);
    }

    private void goToRecvFiles() {
        // TODO GUY
    }

    private void goToMySentFiles() {
        Intent I = new Intent(getApplicationContext(), MySentFiles.class);
        startActivity(I);

    }

    private void goToHome() {
        //todo
    }

    /**
     * Sets nav item i checked if nav is not null.
     * @param item
     */
    protected void setChecked(int item){
        if (nav != null)
            nav.getMenu().getItem(item).setChecked(true);
    }
}
