package com.sd.a3kleingroup;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

/**
    This uses requestCode 10 - if someone else is currently using requestCode 10, I will change mine.
    This uses Result Success code -10.


 */

public class PublicFileSendActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //to add info
    private dbPublicFiles thisFile; //so that have access to the constructor and the hash map for adding it to the firestore db
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String TAG = "Success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_send);



    }





    /*
        Need to think about how this should be structured, should the public files be one collection
        and then inside that there are sub collections which are made up of the userID and then followed by the docs
        which would make queries fast and easy since just look for the userID - if no userID then clearly no 
        public file was setup and hence can inform the user if they have one or inform the user's friends they dont
        have one. This would mainly serve as functionality later. 
     */

    //Some FireStore stuff for the database.
    public void setFIleInfo(){ //can test
        thisFile = new dbPublicFiles("userID", "", "", "", "" );
    }
    public void sendInfoToFirestore(dbPublicFiles someFile){
        // TODO: 2020/04/30 decide if this should be the only collection or this should lead to a sub collection of user ids.
        //chose to add instead of set so that document ID will be generated
        firebaseFirestore.collection("Public Files").add(someFile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Doc snapshot created, ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error adding, exception", e);
            }
        });
    }
}
