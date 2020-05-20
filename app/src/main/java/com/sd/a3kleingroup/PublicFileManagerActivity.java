package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;
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
    Get your files info from the firestore database
    Show the file names and have each file name as clickable - i.e the cardview itself is clickable
    When you click on the file it takes you to a new activity which just shows the people who viewed the file
    As well as lets you delete that file there.
    */


    private String userID = FirebaseAuth.getInstance().getUid(); //User's id
    //private String TAG = "Public File Manager Activity";

    private ArrayList<dbPublicFiles> thisUserFiles = new ArrayList<>();
    private boolean userExists = false;
    RecyclerView recyclerView;
    PublicFileManagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_manager_files);
        thisUserFiles = new ArrayList<>();
        checkUserExists();

        
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.file_manager_recyclerview);
        PublicFileManagerAdapter managerAdapter = new PublicFileManagerAdapter(this, thisUserFiles);
        recyclerView.setAdapter(managerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toast.makeText(this, "UI Done", Toast.LENGTH_SHORT).show();
    }
    public void checkUserExists(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Public Files").whereEqualTo("user_id", userID+"").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        Toast.makeText(PublicFileManagerActivity.this, "user entry does not exist", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        userExists = true;
                        retrieveFiles();
                    }
                }
                else{
                    Toast.makeText(PublicFileManagerActivity.this, "user entry does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void retrieveFiles(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference fileCollectionRef = firebaseFirestore.collection("Public Files");

        Query filesQuery = fileCollectionRef.whereEqualTo("user_id", userID);

        filesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){

                           thisUserFiles.add(new dbPublicFiles(documentSnapshot));
                    }
                    Toast.makeText(PublicFileManagerActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    buildRecyclerView();
                }
                else{
                    Toast.makeText(PublicFileManagerActivity.this, "File retrieval error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
