package com.sd.a3kleingroup.classes.Sending;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.SendFileActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.encryption.AESEncryption;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class to send a local file
 */
public class SendLocalFile {
    private static final String LOG_TAG = "MY_SendLocalFile";
    private Callback callback;
    private String userSending, userReceiving;
    private String filename;
    private InputStream fileToSend;
    FirebaseFirestore db;
    private FirebaseStorage storage;
    private Callback progressCallback;
    private String fileType;

    //
    private StorageReference fileRef;
    private String filePathFirebase;

    /**
     * @param callback
     * @param userSending
     * @param userReceiving
     * @param filename
     * @param fileType application/pdf, or images/jpeg or such
     * @param fileToSend
     * @param db
     * @param storage
     * @param progressCallback Gets called with data: {transferred: sizeTransferred} on a progress listener
     */
    public SendLocalFile(Callback callback, String userSending, String userReceiving, String filename,String fileType, InputStream fileToSend, FirebaseFirestore db, FirebaseStorage storage, Callback progressCallback) {
        this.callback = callback;
        this.userSending = userSending;
        this.userReceiving = userReceiving;
        this.filename = filename;
        this.fileToSend = fileToSend;
        this.db = db;
        this.fileType = fileType;
        this.storage = storage;
        this.progressCallback = progressCallback;
    }


    /**
     * Actually uploads the file and adds to the db.
     */
    public void send() {
        begin();
    }


    /**
     * This gets called at the beginning, to initialize the multi step process.
     */
    private void begin() {
        // can now actually send, since we got a proper email.
        // I now need to save the file to storage
        // Create a storage reference from our app

        StorageReference storageRef = storage.getReference();


        // the path that should be stored on the firebase file system
        filePathFirebase = userSending + "/" + filename.replace('/', '_');
        fileRef = storageRef.child(filePathFirebase);

        InputStream stream = fileToSend;

        AESEncryption encryptor = new AESEncryption();
        InputStream encrypted = encryptor.encrypt(stream);
        // END Encrypted

        UploadTask uploadTask = fileRef.putStream(encrypted);

        // Register observers to listen for when the download is done or if it fails

        uploadTask
                .addOnFailureListener(this::afterFailUploadFile)
                .addOnSuccessListener(snapshot -> afterUploadFile(snapshot, encryptor.getKey()))
                .addOnProgressListener(taskSnapshot -> {
                    // progress update
                    // just set the progress to be transferred/total
                    Log.d(LOG_TAG, "PROGRESS " + taskSnapshot.getBytesTransferred());
                    progressCallback.onSuccess(new HashMap<String, Object>() {{
                        put("transferred", taskSnapshot.getBytesTransferred());
                    }}, "");

                });
    }

    /**
     * This gets called after the file is successfully uploaded. It updates the Database
     */
    private void afterUploadFile(UploadTask.TaskSnapshot taskSnapshot, String key) {
//        Toast.makeText(getApplicationContext(), "Successfully uploaded file", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "Success on upload");

        // reference this, because we need to use this class instance inside a nested class.
        SendLocalFile self = this;

        //first get the file URL. This is async, so need to add oncomplete listener.
        fileRef.getDownloadUrl().addOnCompleteListener(uriTask -> {
            if (!uriTask.isSuccessful()) {
                callback.onFailure("Could not successfully obtain the file URL. Error: (" + uriTask.getException().getMessage() + ")", MyError.ErrorCode.TASK_FAILED);
                return;
            }
            // If file is successfully uploaded, I should update the file DB collection and the agreements collection

            // Get DB instance
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // first add a record to the File collection TODO Make better

            Task<DocumentReference> fileDocRef = db.collection("Files").add(
                    new dbFile(filePathFirebase, filename, userSending, uriTask.getResult().toString(), key, fileType).getHashmap()
            );

            fileDocRef.addOnCompleteListener(task -> {
                DocumentReference ref = task.getResult();
                if (task.isSuccessful() && ref != null) {
                    String fileID = ref.getId();
                    Task<DocumentReference> agreementDocRef = db.collection("Agreements").add(
                            new dbAgreement(fileID, userReceiving, userSending).getHashmap()
                    );
                    agreementDocRef.addOnSuccessListener(aTask -> {
                        afterUpdateDB();
                    }).addOnFailureListener(self::afterFailUpdateDB);

                } else {
                    // This means we didn't upload successfully
                    afterFailUpdateDB(Objects.requireNonNull(task.getException()));
                }
            }).addOnFailureListener(self::afterFailUpdateDB); // if it failed
        });

    }

    /**
     * This gets called after the database is successfully updated.
     * It displays info to the user and cleans up
     */
    private void afterUpdateDB() {

        callback.onSuccess(new HashMap<>(), "Database updated successfully");
        Log.d(LOG_TAG, "Database updated successfully");
    }

    /* Failure handlers, i.e. what happens when stuff fails */

    private void afterFailUploadFile(Exception exception) {
        // Handle unsuccessful uploads

        callback.onFailure("An Error occured (" + exception.getMessage() + ")", MyError.ErrorCode.TASK_FAILED);
        Log.d(LOG_TAG, "Failed to upload file");
    }

    private void afterFailUpdateDB(Exception e) {
        callback.onFailure("An Error occurred while updating database (" + e.getMessage() + ")", MyError.ErrorCode.TASK_FAILED);
        Log.d(LOG_TAG, "Failed to update Database (" + e.getMessage() + ")");
    }

}
