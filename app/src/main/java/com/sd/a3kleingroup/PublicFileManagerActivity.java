package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.PublicFile;
import com.sd.a3kleingroup.classes.UI.PublicFileManagerAdapter;
import com.sd.a3kleingroup.classes.UI.PublicRecyclerViewAdapter;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

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
    private String fileToDeleteID; //need to get this based on a text button with the file ID and name.
    private String TAG = "Public File Manager Activity";

    List<dbPublicFiles> thisUserFiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private PublicFileManagerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager; //will use linear layout

    boolean userEntryExists = false; //assume false until proven true.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userExists();
        if(userEntryExists){
            buildRecyclerView();


        }
        else{
            Toast.makeText(this, "Currently have no files", Toast.LENGTH_SHORT).show();
        }


    }

    private void buildRecyclerView() {
        getInfo();
        mRecyclerView = findViewById(R.id.public_friend_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        ArrayList<dbPublicFiles> files = new ArrayList<>(thisUserFiles);
        mAdapter = new PublicFileManagerAdapter(files);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PublicFileManagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //nothing for now
            }

            @Override
            public void onDeleteClick(int position) {
                deleteEntry(files.get(position).getFileStorage()); // TODO: 2020/05/13 this needs to be the document ID not the file storage, change this later
            }

            @Override
            public void onViewInfoClick(int position) {
                // TODO: 2020/05/13 start an activity to specifically view this file's info
                
            }
        });
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
            userEntryExists = false; //no results for this user and hence must be empty
        }
        else {
            userEntryExists = true; //since it is not empty for this user ID it must exist.
        }
    }



    /*we will need some form of info retrieval so that the user can interact with his files.
    this could include the people who have seen it and more. Should we choose to add that here.
    However this should only be called should the user have a database set up.
    */
    // TODO: 2020/04/30 think of a way to use the file IDs to break up the data so that we can read the info in a reliable fashion. As this will be important for all queries
    public void getInfo(){
      Task<QuerySnapshot> userQuery = publicFilesRef.whereEqualTo("user_id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
              // check to see if there is data.
              if(task.getResult().isEmpty()){ //no data
                  //Informing the user that there is no data contained
                  Toast.makeText(PublicFileManagerActivity.this, "You currently have no files", Toast.LENGTH_LONG).show();
              }
              else{ //inform user there is some data
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
        // TODO: 2020/04/30 check that his actually works and ensure that it doesn't cause issues
        if(!userQuery.getResult().isEmpty()) {
            thisUserFiles = userQuery.getResult().toObjects(dbPublicFiles.class); // if it is not empty then we should get the data from this query.
            Toast.makeText(PublicFileManagerActivity.this, "Info retrieved successfully", Toast.LENGTH_SHORT).show(); //inform the user the info has loaded
        }
    }

    /*
        Code for deleting an entry

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
                //not going to toast here, will rather toast if it fails. Since this gets executed by the deleteAll as well
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Document failed to delete");
                Toast.makeText(PublicFileManagerActivity.this, "Document failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: 2020/04/30 figure out a good way of deleting all of these docs
    /*
       Currently what I am attempting to do is that you click a button which would just enact this method
       this method's goal is to just get all file instances with your id attached and then delete them and hence deleting the files.
       this would make storing the people who viewed my public files dangerous here since if the array contained a sub array with an element of some user deleting his files
       your files would also get deleted.
       A change that might prevent this would be having a viewer file, that has your id, a file id, its name and then an array of emails that have viewed your file.
       However with such an email solution we could just delete like this...
     */
    public void deleteAllFiles(){
        firebaseFirestore.collection("Public File").whereEqualTo("user_id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for( DocumentSnapshot doc : task.getResult()){ //loop through the documents that contain that user's id and delete all his files based on their id
                        deleteEntry(doc.getId()); //delete each specific entry
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed in deleting all files");
                Toast.makeText(PublicFileManagerActivity.this, "Failed in deleting all files", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
