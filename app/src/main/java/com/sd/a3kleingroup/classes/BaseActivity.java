package com.sd.a3kleingroup.classes;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.LoginActivity;
import com.sd.a3kleingroup.MySentFiles;
import com.sd.a3kleingroup.PublicFileManagerActivity;
import com.sd.a3kleingroup.PublicFileReceiveActivity;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.ReceiveFilesActivity;
import com.sd.a3kleingroup.SendFileActivity;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;

public abstract class BaseActivity extends AppCompatActivity {
//    @Override
    protected String LOG_TAG;
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
//                    case R.id.navigation_public_receive_files:
//                        goToPublicReceiveFiles();
//                        return true;
//                    case R.id.navigation_public_manage_files:
//                        goToPublicManageFiles();
//                        return true;
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
        Intent I = new Intent(getApplicationContext(), ReceiveFilesActivity.class);
        startActivity(I);
    }

    private void goToMySentFiles() {
        Intent I = new Intent(getApplicationContext(), MySentFiles.class);
        startActivity(I);

    }

    private void goToHome() {
        //todo
    }

    private void goToPublicManageFiles(){
        Intent I = new Intent(getApplicationContext(), PublicFileManagerActivity.class);
        startActivity(I);
    }

    private void goToPublicReceiveFiles(){
        Intent I = new Intent(getApplicationContext(), PublicFileReceiveActivity.class);
        startActivity(I);
    }

    /**
     * Sets nav item i checked if nav is not null.
     * @param item
     */
    protected void setChecked(int item){
        if (nav != null)
            nav.getMenu().getItem(item).setChecked(true);
    }



    public void getAsync(String collectionName, String docID, Callback cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName).document(docID).get().addOnSuccessListener(documentSnapshot -> {
            Log.d(LOG_TAG, "Got data " + documentSnapshot.getData() + " " + collectionName + " " + docID);
            if (documentSnapshot.getData() == null) {
                cb.onFailure("No data", MyError.ErrorCode.NOT_FOUND);
                System.out.println("NEEE");

            } else {
                cb.onSuccess(documentSnapshot.getData(), "");
                System.out.println("NEEE");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cb.onFailure(e.getMessage(), MyError.ErrorCode.TASK_FAILED);
                System.out.println("NEEE");
            }
        });
    }


    /**
     * Logs in with a temporary user and runs callback on that.
     * @param cb
     */
    public void loginTempUser(Callback cb) {
        // Log IN first
        Log.d("REEEEEE", "HELLO1 " + FirebaseAuth.getInstance().getCurrentUser());
        FirebaseAuth.getInstance().signInWithEmailAndPassword("abc@gmail.com", "abc123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            cb.onSuccess(null, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                            cb.onFailure("signInWithEmail:failure", MyError.ErrorCode.TASK_FAILED);

                        }
                    }
                });
    }
}
