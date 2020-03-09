package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.RecyclerAdapter;

import java.util.ArrayList;

public class ReceiveFilesActivity extends AppCompatActivity {
    FirebaseStorage storage;
    RecyclerView mRecyclerView;
    ArrayList<FileModel> fileModelArrayList = new ArrayList<>();
    RecyclerAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_files);


        setUpFB();
        getDataFromFirebase();
    }


    private void getDataFromFirebase() {
        if(fileModelArrayList.size()>0)
            fileModelArrayList.clear();

        // [START storage_list_all]
        StorageReference listRef = storage.getReference().child("test/");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                        }

//
//                        final Uri[] fileUri = new Uri[1];
//                        final String[] fileName = new String[1];
//                        final String[] fileType = new String[1];

                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.

                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                        @Override
                                        public void onSuccess(StorageMetadata storageMetadata) {

                                            String fileName = storageMetadata.getName();
                                            String fileType = storageMetadata.getContentType();
                                            if(fileName.contains(fileType)){ fileType=""; }
                                            Log.e("MY_LOG", uri.toString());
                                            FileModel file = new FileModel(fileName,fileType, uri);
                                            fileModelArrayList.add(file);
                                            setUpRV();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ReceiveFilesActivity.this, "Error getting file Metadata", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReceiveFilesActivity.this, "Error listing files", Toast.LENGTH_SHORT).show();
                    }
                });

        // [END storage_list_all]

    }

    //Initialize storage field
    private void setUpFB(){
        storage = FirebaseStorage.getInstance();
    }

    private void setUpRV(){
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        Log.e("MY_L", fileModelArrayList.get(0).getUri().toString());
        myAdapter = new RecyclerAdapter(ReceiveFilesActivity.this, fileModelArrayList);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
