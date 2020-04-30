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
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.db.dbUserFriends;

import java.util.ArrayList;
import java.util.List;


//This class serves as a recycler adapter
public class PublicFileReceiveActivity extends AppCompatActivity {

    Button fetchFile; //button to fetch the files
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getUid();
    boolean hasFriends = false; //assume false till proved
    List<dbUserFriends> friends; // TODO: 2020/05/01 replace with friends class
    List<dbUser> dbUsers;

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
                friends = queryDocumentSnapshots.toObjects(dbUserFriends.class); // TODO: 2020/05/01 change to friends class
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure in retrieving friends");
                Toast.makeText(PublicFileReceiveActivity.this, "Some error occurred in getting friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    Okay so assuming that the get friends functions have been done, this means that we have the info of our friends userIDs as well in the friend object
    So what we should do is we should request files from there. Since we want all of our friends we need to display their usernames or their email and let people choose who to view
    BUT we also need to see if they have a public file set up. So for now I assume that the following might be a good order but another does exist:
    1) We get all our friends and show them, we let the user click on a friend and from there the public file will be shown or the user will be told that there is no public file set up
    2) We could check to see if a user has a public file at current.

    I will work with 1 for now and will use the dbUser
     */
    private void getFriendInfo(String friendID){//use passed friend id to get that friends's doc
        firebaseFirestore.collection("Users").document(friendID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //since it should not be empty based on the fact we know at least one friend exists
                dbUsers.add(documentSnapshot.toObject(dbUser.class)); // TODO: 2020/05/01 check that this works
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PublicFileReceiveActivity.this, "Failed to get a friends info", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed in retrieving friend info");
            }
        });
    }
    //use this to allow us to use the getFriendinfo
    private void getAllFriendsInfo(){
        try {
            for (dbUserFriends friend : friends) {
                getFriendInfo(friend.getFriendID()); //just keep adding friends to the list
            }
        }
        catch (Exception e){
                Log.d(TAG, "Error in getAllFriendsInfo " + e);
            }
    }

}
