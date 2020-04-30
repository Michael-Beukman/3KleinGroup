package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.UI.PublicFileReceiveAdapter;

import java.util.ArrayList;
import java.util.List;


//This class serves as a recycler adapter
public class PublicFileReceiveActivity extends AppCompatActivity {

    Button fetchFile; //button to fetch the files
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getUid();
    boolean hasFriends = false; //assume false till proved
    List<String> friends; // TODO: 2020/05/01 replace with friends class

    String TAG = "Public File Receive Activity";
    // TODO: 2020/04/30 make a way to delete information, probably the best way to do this would be to have a public file manage activity 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_public_file_recieve);
        setContentView(R.layout.public_recycler_view); // will require to be changed as super basic

    }

    /*
    Dealing with the friends side
    Will require a compound query with his userID and the bool to check that it is true at least once
     */
    public void hasAFriend(){ //we need to ensure that this user has at least one friend so that we can get the database of public files. otherwise no point
        firebaseFirestore.collection("Friends").whereEqualTo("recipientID",userID).whereEqualTo("accepted",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(PublicFileReceiveActivity.this, "No friends available", Toast.LENGTH_LONG).show();
                    //leave has friends since will be false;
                }
                else{
                    Log.d(TAG, "Has friend");
                    hasFriends = true;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to see if there was at least one friend");
                Toast.makeText(PublicFileReceiveActivity.this, "Error in checking for a friend", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //method to get all our friends, call this after we confirm there is a friend.
    //So we can always assume we will get at least 1 friend
    public void getFriends(){
        firebaseFirestore.collection("Friends").whereEqualTo("recipientID", userID).whereEqualTo("accepted",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG,"Success in retrieving friends");
                friends = queryDocumentSnapshots.toObjects(String.class); // TODO: 2020/05/01 change to friends class
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure in retrieving friends");
                Toast.makeText(PublicFileReceiveActivity.this, "Some error occured in getting friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
