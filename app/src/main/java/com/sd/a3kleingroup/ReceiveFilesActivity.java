package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.RecyclerAdapter;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

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
                                                        HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                                                        FileModel file = new FileModel((String) map.get("filename"),"",(String) map.get("filepath"), (String) map.get("storageURL"));
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

    }

    private void setUpFireStore(){
        db = FirebaseFirestore.getInstance();
    }


    public void downloadFile(FileModel file) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(file.getPath());

        File localFile = null;
        try {
            //TODO check that filename is valid
            localFile = File.createTempFile("temp", null,null);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }


        fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.e(LOG_TAG, taskSnapshot.getBytesTransferred()+"");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(LOG_TAG, exception.getMessage());
            }
        });
        Log.e(LOG_TAG, localFile.getAbsolutePath());

        //Google download manager
//        DownloadManager downloadmanager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri = Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalFilesDir(this, destinationDirectory, fileName + fileExtension);
//
//        downloadmanager.enqueue(request);
    }


    private void setUpRV(){
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                FileModel file = fileModelArrayList.get(position);
                downloadFile(file);
            }
        };
        myAdapter = new RecyclerAdapter(ReceiveFilesActivity.this, fileModelArrayList, listener);
        mRecyclerView.setAdapter(myAdapter);
    }
}
