package com.sd.a3kleingroup;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import com.sd.a3kleingroup.classes.PublicFile;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.io.File;

/**
    This uses requestCode 10 - if someone else is currently using requestCode 10, I will change mine.
    This uses Result Success code -10.

>>>>>>> 05efa0b9bbfc40dabd648ffc5c3b98b43fd14d62

/**
 * This uses requestCode 10 - if someone else is currently using requestCode 10, I will change mine.
 * This uses Result Success code -10.
 */

public class PublicFileSendActivity extends FileChooseActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //to add info
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(); //storage
    StorageReference storageReference = firebaseStorage.getReference();
    private MyError errorHandler;

    private dbPublicFiles thisFile; //so that have access to the constructor and the hash map for adding it to the firestore db
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String TAG = "Public File Send Activity MY_";
    private MyFile fileToSend = new MyFile();
    Uri downloadURL = null; // reset this to null after each upload and it's info no longer needed. That way we can tell if we got the URL
    dbPublicFiles fileToAdd = null; //keep null an upload was successful, when it is successful get the dbPublic file info and set it's parameters and then upload.
    // TODO: 2020/05/01 check to see if this user currently has an entry in the db for public files since we can pull some info from there.

    // Views
    private Button btnChooseFile;
    private TextView txtFilename;
    private TextView lblFilename;
    private Button btnUpload;
    private ProgressBar progressBar;


    public void onCreate(Bundle savedInstanceState) {
        LOG_TAG = TAG;
        setContentView(R.layout.activity_public_file_send);
        super.onCreate(savedInstanceState);
        errorHandler = new MyError(getApplication());
        afterLookAtFile = new afterChooseFile();
        // TODO: 2020/05/01 add interface for user
        setElements();
        setEvents();

    }


    /* UI STUFF */

    /**
     * Sets the above ui variables to their correct elements in the ui.
     */
    void setElements() {
        btnChooseFile = findViewById(R.id.public_file_btnChooseFile);
        btnUpload = findViewById(R.id.public_file_btnSend);

        lblFilename = findViewById(R.id.public_file_lblFilename);
        txtFilename = findViewById(R.id.public_file_txtFilename);
        progressBar = findViewById(R.id.public_file_prgbProgress);
        progressBar.setMax(100);

        cleanUI();
    }

    /**
     * Creates events for the above buttons.
     */
    void setEvents() {
        btnChooseFile.setOnClickListener(new chooseFile());
        txtFilename.addTextChangedListener(new FilenameChange(fileToSend));
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Filename " + fileToSend.getFilename());
            }
        });
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    /**
     * This class is used as a callback after choosing a file.
     * It does things like set the textviews and such.
     */
    class afterChooseFile extends Callback {

        @Override
        public void onSuccess(Map<String, Object> data, String message) {
            if (!data.containsKey("Uri")) {
                this.onFailure("Cannot find Uri in map", MyError.ErrorCode.NOT_FOUND);
                return;
            }
            Uri fileUri = (Uri) data.get("Uri");
            Log.d(LOG_TAG, "URI: " + fileUri + " PATH: " + fileUri.getPath() + " Exists + " + new File(fileUri.getPath()).exists());


            String filePath = fileUri.getPath();
            fileToSend.setFilepath(filePath);
            fileToSend.setUri(fileUri);

            // make filename text visible.
            txtFilename.setVisibility(View.VISIBLE);
            lblFilename.setVisibility(View.VISIBLE);
            txtFilename.setText(filePath);
            // set this button enabled, since now you can send a file.
            btnUpload.setEnabled(true);

            Log.d(LOG_TAG, "User chose File: " + filePath + " get path " + getPath(fileUri));
        }

        @Override
        public void onFailure(String error, MyError.ErrorCode errorCode) {
            errorHandler.displayError(error);
            // Failure. Handle Error
            Log.e(LOG_TAG, error);
        }
    }

    /**
     * This gets called after clicking upload
     */
    void uploadFile() {

        // TODO MATTHEW
        /*
         * Now you should do everything it takes to upload a file, i.e. actually upload the file and update the DB.
         * uploadFile(fileToSend) actually uploads the file to the storage.
         * fileToSend is the file data, with filename, uri, etc.
         * The firebase storage url can only be gotten after uploading it, by using chosenReference.getDownloadUrl().addOnCompleteListener
         */
        /*
         * The control flow is something like this:
         * uploadFile() -> uploadFile(fileToSend) [this uploads the file] -> getDownloadURL [after uploading].
         * You should add the call to addInfoToPublicFiles.
         */
    }

    /*
        Need to think about how this should be structured, should the public files be one collection
        and then inside that there are sub collections which are made up of the userID and then followed by the docs
        which would make queries fast and easy since just look for the userID - if no userID then clearly no 
        public file was setup and hence can inform the user if they have one or inform the user's friends they dont
        have one. This would mainly serve as functionality later. 
     */

    //Some FireStore stuff for the database.
    public void setFIleInfo() { //can test
        thisFile = new dbPublicFiles("", "", "", "", "userID");
    }

    public void sendInfoToFirestore(dbPublicFiles someFile) {
        // TODO: 2020/04/30 decide if this should be the only collection or this should lead to a sub collection of user ids.
        //chose to add instead of set so that document ID will be generated
        firebaseFirestore.collection("Public Files")
                .add(someFile)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Doc snapshot created, ID: " + documentReference.getId());
                        Toast.makeText(PublicFileSendActivity.this, "File successfully sent to database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
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
    public void uploadFile(MyFile fileToSend) {
        if (fileToSend == null) {
            String msg = "file to send is null. Cannot send. Aborting";
            errorHandler.displayError(msg);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);
        btnChooseFile.setEnabled(false);
        // the IS that we will send.
        InputStream stream;
        int totalSize;
        try {
            stream = getContentResolver().openInputStream(fileToSend.getUri());
            totalSize = stream.available();
        } catch (Exception e) {
            errorHandler.displayError("Reading the file failed with message (" + e.getMessage() + ")");
            return;
        }

        StorageReference chosenReference = storageReference.child(userID + "/" + fileToSend.getFilename().replace('/', '_')); //send this to this user's folder.
        UploadTask uploadTask = chosenReference.putStream(stream);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            errorHandler.displaySuccess("File successfully uploaded");

            getDownloadURL(chosenReference); // to get the download URL of this file that was uploaded.
            cleanUI();
        }).addOnFailureListener(e -> {
            errorHandler.displayError("Failed to upload the file");
            cleanUI();
        })
        .addOnProgressListener(taskSnapshot -> {
                    // progress update
                    // just set the progress to be transferred/total
                    Log.d(LOG_TAG, "PROGRESS " + taskSnapshot.getBytesTransferred());
                    progressBar.setProgress((int) (100 * (float) taskSnapshot.getBytesTransferred() / totalSize));
                });
    }


    public void getDownloadURL(StorageReference fileRef){
        fileRef.getDownloadUrl()
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                downloadURL = task.getResult();

                // TODO MATTHEW possibly set the file info now, and then call add infoToPublicFiles
                Log.d(TAG, "Success in getting a download Uri");
            }
            else{
                Log.d(TAG, "Failed to get the download Uri");
            }
        });
    }

    /*
    add information the the firestore about this file that was just uploaded
    call this if the downloadURL is not null.
    and when the fileToAdd is not null - so it has info to add.
     */

    public void addInfoToPublicFiles(){
        firebaseFirestore.collection("Public Files").add(fileToAdd).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "Successfully added the file to database");
                Toast.makeText(PublicFileSendActivity.this, "Successful addition to firebase", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.d(TAG, "Some failure occurred");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Some failure occurred");
            }
        });
    }

    /**
     * Cleans the UI, makes stuff invisible and such
     */
    void cleanUI() {
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setProgress(0);

        // set views default again
        txtFilename.setVisibility(View.INVISIBLE);
        txtFilename.setText("");

        lblFilename.setVisibility(View.INVISIBLE);

        // make send button false, since you cannot send nothing xD
        btnUpload.setEnabled(false);

        btnChooseFile.setEnabled(true);
    }
}

