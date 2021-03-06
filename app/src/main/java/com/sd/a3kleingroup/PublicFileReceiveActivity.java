package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.UI.PublicRecyclerViewAdapter;
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.db.dbUserFriends;

import java.util.ArrayList;
import java.util.List;

/*
    The goal here is to show the user all his friends and then display their profiles - so name and username.
    Then the user can choose who to view and see if they have a public file set up.

 */

public class PublicFileReceiveActivity extends AppCompatActivity {

    Button fetchFile; //button to fetch the files
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getUid();
    boolean hasFriends = false; //assume false till proved
    List<dbUserFriends> friends; // TODO: 2020/05/01 replace with friends class
    List<dbUser> userFriend;


    String TAG = "Public File Receive Activity";
    // TODO: 2020/04/30 make a way to delete information, probably the best way to do this would be to have a public file manage activity 
    private RecyclerView recyclerView;
    private PublicRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager; //will use linear layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_public_file_recieve);
        setContentView(R.layout.activity_main); // will require to be changed as super basic
        // TODO: 2020/05/01 need to make a view for all your friends and then display them for you to choose who you would like to see
        hasAFriend();

        if(hasFriends){
            buildRecyclerView();
        }

        else{
            Toast.makeText(this, "Currently you have no friends", Toast.LENGTH_LONG).show();
        }

    }

    public void buildRecyclerView(){
        /*
        Shows a list of your friends for you to choose who to click on and view their file.
         */

        friends = new ArrayList<dbUserFriends>(); //check
        getFriends();
        userFriend = new ArrayList<dbUser>(); //check
        getAllFriendsInfo();
        recyclerView = findViewById(R.id.public_friend_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ArrayList<dbUser> arrayListUserFriends = new ArrayList<dbUser>(userFriend);
        adapter = new PublicRecyclerViewAdapter(arrayListUserFriends);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PublicRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //create the intent for viewing this user's public file info.
                dbUser userToView = arrayListUserFriends.get(position);
            }
        });
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
        firebaseFirestore.collection("Users").document(friendID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userFriend.add(task.getResult().toObject(dbUser.class)); //use this info to display our friend(name and email, since it is our friend)
                Log.d(TAG, "Getting friend was a success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure to get friend info for: " + friendID + " error: " + e);
                Toast.makeText(PublicFileReceiveActivity.this, "Error in receiving friend", Toast.LENGTH_SHORT).show();
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
