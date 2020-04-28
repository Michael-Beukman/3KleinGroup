package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/**
    This uses requestCode 10, if someone else is currently using requestCode 10, I will change mine.
 */

public class PublicFileSendActivity extends AppCompatActivity {

    Button chooseFile;
    Button uploadFile;
    //need to make some notification for success
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_send);

        //set up for the files
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        chooseFile = findViewById(R.id.public_file_btnChooseFile);
        uploadFile = findViewById(R.id.public_file_btnSend);

        //it hearts you click
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //assuming that we have permission to read our device for its local file since done in first sprint
                if(ContextCompat.checkSelfPermission(PublicFileSendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }
                //else ask user to grant permission
                //requestCode
                ActivityCompat.requestPermissions(PublicFileSendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                //acknowledgement done in a different method
            }
        });
    }
    //acknowledgement of the request for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //check if the requestCode is 10 and if the permission was granted
        if(requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectFile();
        }
        else{
            //inform the user that there is an issue
            Toast.makeText(PublicFileSendActivity.this, "Permission was not granted...", Toast.LENGTH_LONG).show();
        }
    }

    private void selectFile(){

    }



}
