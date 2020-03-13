package com.sd.a3kleingroup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;
import com.sd.a3kleingroup.classes.Utils;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;


public class SendFileActivity extends BaseActivity {
    private final int FILE_RESULT_SUCCESS = -1;
    private final int FILE_REQUEST_CODE = 1;
    private final String LOG_TAG = "SEND_FILE_ACTIVITY";

    // firebase storage reference
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // firebase authentication
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button btnChooseFile;
    private Button btnSend;
    private EditText txtEmail;
    private EditText txtFilename;
    private TextView lblFilename;
    private ProgressBar progressBar;

    private MyError errorHandler;

    private MyFile file; // the file that will be sent.

    //what happens when the file is chosen
    private class chooseFile implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // set btn disabled, so it doesn't double click
            view.setEnabled(false);
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            // use pick instead to get a file URI.
//            Intent chooseFile = new Intent(Intent.ACTION_PICK);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, FILE_REQUEST_CODE);
        }
    }

    private class sendFile implements View.OnClickListener {

        private String filePathFirebase;
        private String filename;
        private FirebaseUser user;
        private String userToReceiveID;
        private StorageReference fileRef; // the fileRef from firebase

        @Override
        public void onClick(View view) {
            // disable to prevent double clicks
            view.setEnabled(false);

            // first find user that the User wants to send to
            Callback myUserCallback = new Callback() {
                @Override
                public void onSuccess(HashMap<String, Object> data, String message) {
                    afterGetEmail(data, message);
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    afterFailGetEmail(error, errorCode);
                }
            };
            Utils instance = Utils.getInstance();
            String userEmail = txtEmail.getText().toString();
            instance.getUserFromEmail(userEmail, myUserCallback);
        }

        /**
         * This gets called after we successfully get the email. Then we can continue with the process.
         *
         * @param data     The data from Utils.GetEmail. userID: <userID>
         * @param message: Extra info/message
         */
        private void afterGetEmail(HashMap<String, Object> data, String message) {
            // can now actually send, since we got a proper email.
            userToReceiveID = data.containsKey("userID") ? (String) data.get("userID") : "-1";
            Log.d(LOG_TAG, "Found userID from email: " + userToReceiveID);

            // I now need to save the file to storage
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // currently logged in user
            user = auth.getCurrentUser();
            if (user == null) {
                // if the user is null, we need to go to the login activity first.
                goToLogin();
                return;
            }

            // pretty name
            filename = txtFilename.getText().toString();

            // the path that should be stored on the firebase file system
            filePathFirebase = user.getUid() + "/" + filename.replace('/', '_');
            fileRef = storageRef.child(filePathFirebase);


            InputStream stream;

            int totalSize;
            try {
                stream = getContentResolver().openInputStream(file.getUri());
                totalSize = stream.available();
            } catch (Exception e) {
                errorHandler.displayError("Reading the file failed with message (" + e.getMessage() + ")");
                return;
            }

            UploadTask uploadTask = fileRef.putStream(stream);
            // make the progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // Register observers to listen for when the download is done or if it fails
            int finalTotalSize = totalSize;

            uploadTask
                    .addOnFailureListener(this::afterFailUploadFile)
                    .addOnSuccessListener(this::afterUploadFile)
                    .addOnProgressListener(taskSnapshot -> {
                        // progress update
                        // just set the progress to be transferred/total
                        Log.d(LOG_TAG, "PROGRESS " + taskSnapshot.getBytesTransferred() + " / " + finalTotalSize + " = " + (int) (100 * ((float) taskSnapshot.getBytesTransferred() / (float) finalTotalSize)));
                        progressBar.setProgress(
                                (int) (100 * ((float) taskSnapshot.getBytesTransferred() / (float) finalTotalSize))
                        );
                    });
        }

        /**
         * This gets called after the file is successfully uploaded. It updates the Database
         */
        private void afterUploadFile(UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(getApplicationContext(), "Successfully uploaded file", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Success on upload");
            progressBar.setVisibility(View.INVISIBLE);

            // reference this, because we need to use this class instance inside a nested class.
            sendFile self = this;
            //first get the file URL. This is async, so need to add oncomplete listener.
            fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> uriTask) {
                    if (!uriTask.isSuccessful()) {
                        errorHandler.displayError("Could not successfully obtain the file URL. Error: (" + uriTask.getException().getMessage() + ")");
                        return;
                    }
                    // If file is successfully uploaded, I should update the file DB collection and the agreements collection

                    // Get DB instance
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // first add a record to the File collection TODO Make better

                    Task<DocumentReference> fileDocRef = db.collection("Files").add(
                            new dbFile(filePathFirebase, filename, userToReceiveID, uriTask.getResult().toString()).getHashmap()
                    );

                    // This means we didn't upload successfully
                    fileDocRef.addOnCompleteListener(task -> {
                        DocumentReference ref = task.getResult();
                        if (task.isSuccessful() && ref != null) {
                            String fileID = ref.getId();
                            Task<DocumentReference> agreementDocRef = db.collection("Agreements").add(
                                    new dbAgreement(fileID, userToReceiveID, user.getUid()).getHashmap()
                            );
                            agreementDocRef.addOnSuccessListener(aTask -> {
                                afterUpdateDB();
                            }).addOnFailureListener(self::afterFailUpdateDB);

                        } else {
                            afterFailUpdateDB(Objects.requireNonNull(task.getException()));
                        }
                    }).addOnFailureListener(self::afterFailUpdateDB); // if it failed
                }
            });

        }

        /**
         * This gets called after the database is successfully updated.
         * It displays info to the user and cleans up
         */
        private void afterUpdateDB() {

            errorHandler.displaySuccess("Database updated successfully");
            Log.d(LOG_TAG, "Database updated successfully");


            cleanUp();
        }
        /* Failure handlers, i.e. what happens when stuff fails */

        private void afterFailGetEmail(String error, MyError.ErrorCode errorCode) {
            Log.d(LOG_TAG, "User Email failed with message (" + error + ") and code + (" + errorCode + ")");

            errorHandler.displayError("That user does not exist. Please ask them to sign up first.");
            cleanUp();
        }

        private void afterFailUploadFile(Exception exception) {
            // Handle unsuccessful uploads
            progressBar.setVisibility(View.INVISIBLE);
            errorHandler.displayError("An Error occured (" + exception.getMessage() + ")");
            Log.d(LOG_TAG, "Failed to upload file");
            cleanUp();
        }

        private void afterFailUpdateDB(Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            errorHandler.displayError("An Error occurred while updating database (" + e.getMessage() + ")");
            Log.d(LOG_TAG, "Failed to update Database (" + e.getMessage() + ")");
            cleanUp();
        }

        /**
         * Cleans up, after success or failure.
         */
        private void cleanUp() {

            //enable btn again
            btnSend.setEnabled(true);

            // set views default again
            txtFilename.setVisibility(View.INVISIBLE);
            txtFilename.setText("");

            lblFilename.setVisibility(View.INVISIBLE);

            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }
    }

    private class FilenameChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // set the filename when the editText changes
            file.setFilename(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_REQUEST_CODE) {
            // then we know that this was the file choosing result.
            if (resultCode == FILE_RESULT_SUCCESS) {
                // Then it is a success
                Uri fileUri = data.getData();
                Log.d(LOG_TAG, "URI: " + fileUri + " PATH: " + fileUri.getPath() + " Exists + " + new File(fileUri.getPath()).exists());

                String filePath = fileUri.getPath();
                file.setFilepath(filePath);
                file.setUri(fileUri);

                // make filename text visible.
                txtFilename.setVisibility(View.VISIBLE);
                lblFilename.setVisibility(View.VISIBLE);
                txtFilename.setText(filePath);
                // set this button enabled, since now you can send a file.
                btnSend.setEnabled(true);

                Log.d(LOG_TAG, "User chose File: " + filePath + " get path " + getPath(fileUri));
            } else {

                errorHandler.displayError("Choosing file failed with code (" + resultCode + ")");
                // Failure. Handle Error
                Log.e(LOG_TAG, "File Error with code " + resultCode);
            }
            // set enabled again, since we're done
            btnChooseFile.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        setElements();
        setEvents();
        file = new MyFile();
        errorHandler = new MyError(getApplication());
        MyFirebaseMessagingService x = new MyFirebaseMessagingService();
        x.getToken();
        new GoogleApiAvailability().makeGooglePlayServicesAvailable(this);
    }

    /**
     * Sets events
     */
    private void setEvents() {
        btnChooseFile.setOnClickListener(new chooseFile());
        btnSend.setOnClickListener(new sendFile());
        txtFilename.addTextChangedListener(new FilenameChange());
    }

    /**
     * Assigns the Element variables;
     */
    private void setElements() {
        btnChooseFile = findViewById(R.id.sf_btnChooseFile);
        btnSend = findViewById(R.id.sf_btnSend);
        txtEmail = findViewById(R.id.sf_txtRecipientEmail);
        txtFilename = findViewById(R.id.sf_txtFilename);
        lblFilename = findViewById(R.id.sf_lblFilename);
        progressBar = findViewById(R.id.sf_prgbProgress);


        // change default visibility
        txtFilename.setVisibility(View.INVISIBLE);
        lblFilename.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        // make send button false, since you cannot send nothing xD
        btnSend.setEnabled(false);

        // change min and max
        // progressBar.setMin(0);
        progressBar.setMax(100);


    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
