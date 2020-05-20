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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.UI.PublicFileInfoAdapter;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FileManagerViewFileInfoActivity extends AppCompatActivity {

    Button deletefile;
    RecyclerView recyclerView;
    TextView txtHeader;
    TextView txtFriendsHeader;

    String LOG_TAG = "MY_FileManagerViewFileInfoActivity";
    /**
     * The current file we're dealing with
     */
    dbPublicFiles currentFile;
    String userID = FirebaseAuth.getInstance().getUid();
    MyError errorhandler;
    int numDeleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager_file_info);
        errorhandler = new MyError(FileManagerViewFileInfoActivity.this);
        setElements();
        getData();
        setEvents();

        getWhoViewedData();
    }


    public void setElements() {
        deletefile = findViewById(R.id.public_file_delete_file);
        recyclerView = findViewById(R.id.file_manager_info_recycler);
        txtHeader = findViewById(R.id.fmi_txtHeader);
        txtFriendsHeader = findViewById(R.id.fmi_txtHeaderViewFiles);
        txtHeader.setText("Loading...");
        txtFriendsHeader.setText("Loading...");
    }

    public void setEvents() {
        deletefile.setOnClickListener(this::totallyDeleteFile);
    }


    public void getData() {//check and see if the data exists
        Intent b = getIntent();
        if (b != null && b.hasExtra("fileHashmap") && b.hasExtra("fileID")) {
            HashMap<String, String> hashMap = (HashMap<String, String>) b.getSerializableExtra("fileHashmap");
            String id = b.getStringExtra("fileID");
            currentFile = new dbPublicFiles(hashMap);
            currentFile.setID(id);
            Log.d(LOG_TAG, "We received this file from the previous activity" + currentFile.getHashmap() + " and id = " + currentFile.getID());
            txtHeader.setText("Looking at file: " + currentFile.getFileName());
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
//        if(getIntent().hasExtra("File name") && getIntent().hasExtra("File path")){//if this data exists
//            filename = getIntent().getStringExtra("File name");
//            filepath = getIntent().getStringExtra("File path");
//        }
//        else{
//            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * Does all of the deleting
     *
     * @param v
     */
    private void totallyDeleteFile(View v) {
        if (currentFile == null) {
            errorhandler.displayError("Something went wrong with the file. Please exit and try again later");
            return;
        }
        numDeleted = 0;
        try {
            deleteFileFirestore();
        } catch (Exception e) {
            errorhandler.displayError("Some error occurred while deleting the file");
        }
        try {
            deleteFileStorage();
        } catch (Exception e) {
            errorhandler.displayError("Some error occurred while deleting the file");

        }
    }

    private void deleteFileStorage() { // should work
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference fileToDelete = storageReference.child(userID).child(currentFile.getFileName());
        fileToDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    errorhandler.displaySuccess("Successfully deleted the file from the cloud file system");
                    numDeleted++;
                    checkAfterDelete();
                } else {
                    errorhandler.displayError("Some error occurred while deleting the file");
                }
            }
        });
    }

    private void deleteFileFirestore() {
        FirebaseFirestore.getInstance().collection("Public Files")
                .document(currentFile.getID())
                .delete().addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                errorhandler.displaySuccess("Successfully deleted the file from the database");
                numDeleted++;
                checkAfterDelete();
            } else {
                errorhandler.displayError("Some error occurred while deleting the file");
            }
        });
    }

    /**
     * This function gets called after deleting from firestore, and from storage.
     * if numDeleted == 2, we go to the file manage activity, because there's nothing left to be done here
     */
    private void checkAfterDelete() {
        if (numDeleted == 2) {
            Intent i = new Intent(getApplicationContext(), PublicFileManagerActivity.class);
            startActivity(i);
        }
    }

    /**
     * This checks the WhoViewedPublicFiles collection and checks who viewed this file.
     * It also then populates the recyclerview and stuff.
     */
    private void getWhoViewedData() {
        if (currentFile == null) {
            errorhandler.displayError("Something went wrong with the file. Please exit and try again later");
            return;
        }
        FirebaseFirestore.getInstance().collection("WhoViewedPublicFiles")
                .document(currentFile.getID())
                .get().addOnCompleteListener(t -> {
            ArrayList<String> values = new ArrayList<>();
            if (t.isSuccessful() && t.getResult() != null && t.getResult().exists()) {
                Log.d(LOG_TAG, " The get whoViewed succeeded and the document exists");
                try {
                    values = (ArrayList<String>) t.getResult().get("Users");
                } catch (Exception e) {
                    Log.d(LOG_TAG, " We failed to set the values array because of " + e.getMessage());
                }
            } else {
                Log.d(LOG_TAG, " The whoViewed either doesn't exist or failed.");
            }

            if (values.size() == 0) {
                values.add("No Friends have viewed this file");
            }
            Log.d(LOG_TAG, "Populating recyclerview " + Arrays.toString(values.toArray()));
            // now we make the recyclerview do things
            recyclerView.setAdapter(new PublicFileInfoAdapter(getApplicationContext(), values));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            txtFriendsHeader.setText("Friends who have viewed this file");
        });
    }
}
