package com.sd.a3kleingroup;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.io.File;

/**
    This uses requestCode 10 - if someone else is currently using requestCode 10, I will change mine.
    This uses Result Success code -10.


 */

public class PublicFileSendActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //to add info
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); //storage
    StorageReference storageReference = firebaseStorage.getReference();

    private dbPublicFiles thisFile; //so that have access to the constructor and the hash map for adding it to the firestore db
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String TAG = "Public File Send Activity";
    Uri downloadURL = null; // reset this to null after each upload and it's info no longer needed. That way we can tell if we got the URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_send);
        // TODO: 2020/05/01 add interface for user 


    }





    /*
        Need to think about how this should be structured, should the public files be one collection
        and then inside that there are sub collections which are made up of the userID and then followed by the docs
        which would make queries fast and easy since just look for the userID - if no userID then clearly no 
        public file was setup and hence can inform the user if they have one or inform the user's friends they dont
        have one. This would mainly serve as functionality later. 
     */

    //Some FireStore stuff for the database.
    public void setFIleInfo(){ //can test
        thisFile = new dbPublicFiles("", "", "", "", "userID");
    }

    public void sendInfoToFirestore(dbPublicFiles someFile){
        // TODO: 2020/04/30 decide if this should be the only collection or this should lead to a sub collection of user ids.
        //chose to add instead of set so that document ID will be generated
        firebaseFirestore.collection("Public Files").add(someFile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Doc snapshot created, ID: " + documentReference.getId());
                Toast.makeText(PublicFileSendActivity.this, "File successfully sent to database", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error adding, exception", e);
                Toast.makeText(PublicFileSendActivity.this, "Error in uploading to database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    Actually sending it to storage now - uploading
     */
    public void uploadFile(Uri fileChosen){ // assume that this is created using the Uri.fromFile(new File(path)) method
        StorageReference chosenReference = storageReference.child(userID + fileChosen.getLastPathSegment()); //send this to this user's folder.
        UploadTask uploadTask = chosenReference.putFile(fileChosen);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Succeeded to upload the file");
                Toast.makeText(PublicFileSendActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to upload the file");
                Toast.makeText(PublicFileSendActivity.this, "File failed to upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = ( 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                //Show this somewhere
                Log.d(TAG, progress + "%" );
            }
        });
    }
    public void getDownloadURL(Uri fileUploaded){
        final StorageReference reference = storageReference.child(userID + fileUploaded); // TODO: 2020/05/01 check that this is fine
        UploadTask uploadTask = reference.putFile(fileUploaded);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();

                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    downloadURL = task.getResult();
                    Log.d(TAG, "Success in getting a download Uri");
                }
                else{
                    Log.d(TAG, "Failed to get the download Uri");
                }
            }
        });
    }

}
