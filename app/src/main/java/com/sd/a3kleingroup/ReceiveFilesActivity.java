package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.RecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveFilesActivity extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<FileModel> fileModelArrayList = new ArrayList<>();
    RecyclerAdapter myAdapter;
    FirebaseUser user;
    private static final String LOG_TAG="RecievFilesctivity_LOG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_files);


        setUpFireStore();
        determineCurrentUser();
        getDataFromFirebase();
    }

    private void determineCurrentUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
        }
    }


    private void getDataFromFirebase() {
        if(fileModelArrayList.size()>0)
            fileModelArrayList.clear();

        String userID = user.getUid();
        //TODO remove this when have data for guy
        userID="En8fRBqxPiZ13HvOabUx7uOXN2T2";

        // [START get_multiple]
        db.collection("Agreements")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<String> fileIDs = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fileIDs.add((String) document.getData().get("fileID"));
                            }

                            // [START get_url's]
                            db.collection("Files")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if(fileIDs.contains(document.getId())){
                                                        HashMap<String, String> map = (HashMap<String, String>) document.getData().get("hashmap");
                                                        FileModel file = new FileModel((String) map.get("filename"),"",(String) map.get("storageURL"));
                                                        fileModelArrayList.add(file);
                                                    }
                                                }
                                                setUpRV();
                                            } else {
                                                Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                            // [END get_url's]

                        } else {
                            Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple

        // [START storage_list_all]
//        StorageReference listRef = storage.getReference().child("test/");
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
////                        for (StorageReference prefix : listResult.getPrefixes()) {
////                            // All the prefixes under listRef.
////                            // You may call listAll() recursively on them.
////                        }
//
////
////                        final Uri[] fileUri = new Uri[1];
////                        final String[] fileName = new String[1];
////                        final String[] fileType = new String[1];
//
//                        for (StorageReference item : listResult.getItems()) {
////                            // All the items under listRef.
//
//                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//
//                                    item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                                        @Override
//                                        public void onSuccess(StorageMetadata storageMetadata) {
//
//                                            String fileName = storageMetadata.getName();
//                                            String fileType = storageMetadata.getContentType();
//                                            if(fileName.contains(fileType)){ fileType=""; }
//                                            Log.e("MY_LOG", uri.toString());
//                                            FileModel file = new FileModel(fileName,fileType, uri);
//                                            fileModelArrayList.add(file);
//                                            setUpRV();
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(ReceiveFilesActivity.this, "Error getting file Metadata", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            });
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(ReceiveFilesActivity.this, "Error listing files", Toast.LENGTH_SHORT).show();
//                    }
//                });

        // [END storage_list_all]

    }

    private void setUpFireStore(){
        db = FirebaseFirestore.getInstance();
    }

    private void setUpRV(){
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        //Log.e("MY_L", fileModelArrayList.get(0).getUrl());
        myAdapter = new RecyclerAdapter(ReceiveFilesActivity.this, fileModelArrayList);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
