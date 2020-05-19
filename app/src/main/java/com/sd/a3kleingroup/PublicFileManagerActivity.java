package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.sd.a3kleingroup.classes.db.dbPublicFileManager;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.util.ArrayList;
import java.util.List;

public class PublicFileManagerActivity extends AppCompatActivity {

    /*goal of this activity:
    get your friends userIDs
    Populate the screen with a list of friends and their public databases
    If a friend doesn't have a database do not show them or inform the user in that area (text/button) no database.
    */

    //private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //The database as a whole
    /*
        Below should be true for all users and allow checking to see if a user does have an entry in the database
        So we can just check our queries against user's ID being a part of those in the collection
     */
    //private CollectionReference publicFilesRef = firebaseFirestore.collection("Public Files");

    //private String userID = FirebaseAuth.getInstance().getUid(); //User's id
    //private String fileToDeleteID; //need to get this based on a text button with the file ID and name.
    //private String TAG = "Public File Manager Activity";

    private ArrayList<dbPublicFiles> thisUserFiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private PublicFileManagerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager; //will use linear layout
    //private DocumentSnapshot lastQueriedDoc;
    //private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_friend_recycler);
        buildRecyclerView();



    }

    private void buildRecyclerView() {
        dbPublicFiles test = new dbPublicFiles("","testName","testPath","testStore","testID");
        dbPublicFiles test2 = new dbPublicFiles("","testName2","testPath2","testStore2","testID2");
        thisUserFiles.add(test);
        thisUserFiles.add(test2);
        mRecyclerView = findViewById(R.id.public_files_manage_files_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(PublicFileManagerActivity.this);
        mAdapter = new PublicFileManagerAdapter(thisUserFiles);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        /*
        mAdapter.setOnItemClickListener(new PublicFileManagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //nothing for now
            }

            @Override
            public void onDeleteClick(int position) {
                // TODO: 2020/05/13 this needs to be the document ID not the file storage, change this later
            }

            @Override
            public void onViewInfoClick(int position) {
                // TODO: 2020/05/13 start an activity to specifically view this file's info
                
            }
        });
        */

    }

    private void getFiles(){ //getting this user's files
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference publicFilesRef = firebaseFirestore.collection("Public Files");

        Query filesQuery = publicFilesRef.whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("file_name", Query.Direction.DESCENDING);
        filesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        dbPublicFiles file = documentSnapshot.toObject(dbPublicFiles.class);
                        thisUserFiles.add(file);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(PublicFileManagerActivity.this, "Could not retrieve files", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initRecyclerView(){ //setting up the recycler views
        if(mAdapter == null){
            mAdapter = new PublicFileManagerAdapter(thisUserFiles);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }


}
