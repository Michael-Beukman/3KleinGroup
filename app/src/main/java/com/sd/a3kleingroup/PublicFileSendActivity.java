package com.sd.a3kleingroup;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

/**
>>>>>>> 05efa0b9bbfc40dabd648ffc5c3b98b43fd14d62
 **/

public class PublicFileSendActivity extends FileChooseActivity {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); //storage
    StorageReference storageReference = firebaseStorage.getReference();
    private MyError errorHandler;

    private dbPublicFiles thisFile; //so that have access to the constructor and the hash map for adding it to the firestore db
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String TAG = "Public File Send Activity MY_";
    private MyFile fileToSend = new MyFile();
    Uri downloadURL = null; // reset this to null after each upload and it's info no longer needed. That way we can tell if we got the URL
    dbPublicFiles fileToAdd = null; //keep null an upload was successful, when it is successful get the dbPublic file info and set it's parameters and then upload.
    Uri selectedFile;
    // TODO: 2020/05/01 check to see if this user currently has an entry in the db for public files since we can pull some info from there.

    // Views
    private Button uploadFileButton;
    private Button chooseFileButton;
    private EditText fileName;
    private ProgressBar progressBar;
    private TextView progressText;
    boolean success = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_send_file);
        setElements();
        setEvents();

    }


    /* UI STUFF */

    /**
     * Sets the above ui variables to their correct elements in the ui.
     */
    void setElements() {
        uploadFileButton = findViewById(R.id.public_send_file_button);
        chooseFileButton = findViewById(R.id.public_send_file_choose_file_button);
        fileName =  findViewById(R.id.public_file_send_file_name_edit_text);
        progressBar = findViewById(R.id.public_file_sendprogressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressText = findViewById(R.id.public_send_progress_text);
        progressText.setVisibility(View.INVISIBLE);
    }

    void cleanUI(){
        success = false;
        fileName.setText("");
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
        downloadURL = null;
    }

    /**
     * Creates events for the above buttons.
     */
    void setEvents() {
       uploadFileButton.setOnClickListener(v -> {
           if(fileToSend.getUri() != null){
               upload(fileToSend);
           }
           else{
               Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
           }
       });

       chooseFileButton.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(PublicFileSendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                selectFile();
            }
            else{
                ActivityCompat.requestPermissions(PublicFileSendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 322);
            }
       });

       //make text editable
    }

    //acknowledge being allowed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 322 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectFile();
        }
        else{
            Toast.makeText(this, "External storage not allowed, please provide permission", Toast.LENGTH_LONG).show();
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*"); //let it be any file type
        intent.setAction(Intent.ACTION_GET_CONTENT); //to fetch file
        startActivityForResult(intent,223);
    }

    public void upload(MyFile myFile){
        String name = fileName.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        final StorageReference ref = storageReference.child(userID).child(name); //check that this makes sense
       UploadTask uploadTask = (UploadTask) ref.putFile(myFile.getUri()).addOnSuccessListener(taskSnapshot -> {
           //notify the user of success

           Toast.makeText(PublicFileSendActivity.this, "Successfully uploaded file", Toast.LENGTH_SHORT).show();
           taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri ->
                   success = true).addOnFailureListener(e ->
                   Toast.makeText(PublicFileSendActivity.this, "Failed in getting the uri", Toast.LENGTH_SHORT).show()
           ).addOnCompleteListener(task -> {
               String s = task.getResult().getPath().toString();
               Toast.makeText(PublicFileSendActivity.this, s, Toast.LENGTH_SHORT).show();
           });
           fileToSend.setFilename(fileName.getText().toString());
           cleanUI();
       }).addOnFailureListener(e -> {
            //notify the user of some failure
            Toast.makeText(PublicFileSendActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(taskSnapshot -> {
            //keep track of file upload progress
            progressText.setText("Progress " + taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()*100 +"%");
            progressBar.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()*100), true);
        });
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if( !task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();

            }
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri downloadUri = task.getResult();
                Toast.makeText(PublicFileSendActivity.this, "success", Toast.LENGTH_SHORT).show();
                databaseInfo(downloadUri.toString());
            }
            else{
                //other options
            }
        });
    }

    //check if the user successfully selected a file
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 223 && resultCode == RESULT_OK && data != null){
            fileToSend.setUri(data.getData()); //getting the Uri
            fileName.setText(fileToSend.getUri()+"");

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }


    // TODO: 2020/05/18 cant get the below to work, everything else seems to be working...

    public void databaseInfo(String downloadURL) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //to add info
        dbPublicFiles file = new dbPublicFiles("", fileToSend.getFilename()+"", filepath(fileToSend.getFilename(), userID)+"", downloadURL+"", userID+"");
        firebaseFirestore.collection("Public Files").add(file.getHashmap()).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PublicFileSendActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String filepath(String filename, String userID){
        String toReturn = userID.concat("/");
        toReturn = toReturn.concat(changeName(filename));
        return toReturn;
    }
    public String changeName(String filename){
        String name = "";
        for(int i = 0 ; i < filename.length(); i++){
            if(filename.charAt(i) == '/'){
                name = name.concat("_");
            }
            else{
                name = name.concat(filename.charAt(i) +"");
            }
        }
        return name;
    }
}

