package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

/**
    This uses requestCode 10 - if someone else is currently using requestCode 10, I will change mine.
    This uses Result Success code -10.


 */

public class PublicFileSendActivity extends AppCompatActivity {

    private final int PUBLIC_FILE_RESULT_SUCCESS = -10;
    private final int PUBLIC_FILE_REQUEST_CODE = 10; //permission request
    private final int PUBLIC_FILE_REQUEST_FILE_CODE = 11;
    private final String LOG_TAG = "MY_PUBLIC_FILE_SEND_ACTIVITY";

    //Interaction for user
    Button chooseFile;
    Button uploadFile;

    //Firebase objects
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); //where the files will be sent
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //progress bar to display to user
    private ProgressBar progressBar;

    //our classes
    private MyFile publicFile;
    private MyError errorHandler;
    private dbPublicFiles fileToMakePublic;
    //private dbUser thisUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_send);

        chooseFile = findViewById(R.id.public_file_btnChooseFile);
        uploadFile = findViewById(R.id.public_file_btnSend);
        //need some where to display a notification else wise just toast.

        //choose file button
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override //how to prevent double clicking
            public void onClick(View v) {
                //need to ensure we have permission to read the storage for the device
                if(ContextCompat.checkSelfPermission(PublicFileSendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }
                //Need to get permission
                else{
                    //Ask user to allow, will handle the user allowing in another method
                    ActivityCompat.requestPermissions(PublicFileSendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PUBLIC_FILE_REQUEST_CODE);
                }
            }
        });

    }

    //to get a file
    private void selectFile() {
        //Goal is to let the user select a file using the file manager => intent will be needed
        Intent publicSelect = new Intent();
        publicSelect.setType("*/*");
        publicSelect.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(publicSelect, PUBLIC_FILE_REQUEST_FILE_CODE);
    }

    //Method handling if the user gave us permission to read external storage
    //will serve as an acknowledgement
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //need to ensure that the request code and that permission is granted
        if(requestCode == PUBLIC_FILE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //give the user a second chance to choose a pdf assuming they accept
            selectFile();
        }
        //inform the user that they did not grant access
        else{
            Toast.makeText(PublicFileSendActivity.this, "Permission was denied", Toast.LENGTH_LONG).show();
        }
    }

    //will be invoked in select file.
    //just need to see if user selected a file or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //will check if the user selected a file
        //resultCode is just to see if the task was successfully completed(opening and closing of file manager).
        super.onActivityResult(requestCode, resultCode, data); /** this might be wrong **/
        if (requestCode == PUBLIC_FILE_REQUEST_FILE_CODE && resultCode == RESULT_OK && data != null) {
            publicFile.setUri(data.getData()); //setting the uri of the file selected since getData() should get the uri
        } else {
            Toast.makeText(PublicFileSendActivity.this, "A file was not selected", Toast.LENGTH_LONG).show(); //inform the user a file was not selected
        }
    }
}
