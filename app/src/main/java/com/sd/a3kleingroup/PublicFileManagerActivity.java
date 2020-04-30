package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.PublicFile;

import java.util.ArrayList;
import java.util.List;

public class PublicFileManagerActivity extends AppCompatActivity {

    /*goal of this activity:
    get your friends userIDs
    Populate the screen with a list of friends and their public databases
    If a friend doesn't have a database do not show them or inform the user in that area (text/button) no database.
    */

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //The database as a whole
    /*
        Below should be true for all users and allow checking to see if a user does have an entry in the database
        So we can just check our queries against user's ID being a part of those in the collection
     */
    private CollectionReference publicFilesRef = firebaseFirestore.collection("Public Files");

    private String userID = FirebaseAuth.getInstance().getUid(); //User's id
    private String fileToDeleteID;
    private String TAG;

    List<PublicFile> thisUserFiles;

    boolean userEntryExists = false; //assume false until proven true.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_manager);
        /*
            Code to add
         */

        deleteEntry(fileToDeleteID);
    }

    /*
    code for getting a user
     */
    public void userExists(){
        // TODO: 2020/04/30 check against the receive files activity that this is fine

        Task<QuerySnapshot> userQuery = publicFilesRef.whereEqualTo("user_id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PublicFileManagerActivity.this, "Success, user exists", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PublicFileManagerActivity.this, "Failed to see if user exists", Toast.LENGTH_SHORT).show();
                return; //leave here then
            }
        });
        if(userQuery.getResult().isEmpty()){
            userEntryExists = false;
        }
        userEntryExists = true; //since it is not empty for this user ID it must exist.
    }
    /*we will need some form of info retrieval so that the user can interact with his files.
    this could include the people who have seen it and more.
    however this should only be called should the user have a database set up.
    */
    // TODO: 2020/04/30 think of a way to use the file IDs to break up the data so that we can read the info in a reliable fashion. As this will be important for all queries
    public void getInfo(){
      Task<QuerySnapshot> userQuery = publicFilesRef.whereEqualTo("user_id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
              // check to see if there is data.
              if(task.getResult().isEmpty()){
                  //Informing the user that there is no data contained
                  Toast.makeText(PublicFileManagerActivity.this, "You currently have no files", Toast.LENGTH_LONG).show();
              }
              else{
                Toast.makeText(PublicFileManagerActivity.this, "Loading files", Toast.LENGTH_SHORT);
              }
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              //if we fail, we toast and run
              Toast.makeText(PublicFileManagerActivity.this, "There was an error in receiving your files", Toast.LENGTH_LONG).show();
              return;
          }
      });
        //process the data, so here we need to decide what we wish to show the user
        thisUserFiles = userQuery.getResult().toObjects(PublicFile.class);
    }

    /*
        Code for deleting entries
     */

    public void deleteEntry(String fileID){
        //things we know for certain right now
        //userID, and the collection so we will delete using the fileID, so that will be the string we take.
        //The file ID should be unique to a document within this collection.
        firebaseFirestore.collection("Public Files").document(fileID).delete().
                addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Document was deleted from public file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Document failed to delete");
            }
        });
    }

    // TODO: 2020/04/30 figure out a good way of deleting all of these docs
    /*
    Currently this might be storing an arrayList of all the fileID's and then deleting them
     */
    public void deleteAllFiles(){

    }


    /*
        Code for getting this user's files and their info.
        Will consist of doing queries on this user's ID in the public files database
     */

}
