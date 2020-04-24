package com.sd.a3kleingroup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.Sending.SendLocalFile;
import com.sd.a3kleingroup.classes.callbacks.ArrayCallback;
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.encryption.AESEncryption;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;
import com.sd.a3kleingroup.classes.Utils;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.querying.GetAgreementsUsersFiles;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class SendFileActivity extends BaseActivity {
    private final int FILE_RESULT_SUCCESS = -1;
    private final int FILE_REQUEST_CODE = 1;
    private final String LOG_TAG = "MY_SEND_FILE_ACTIVITY";

    // firebase storage reference
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // firebase authentication
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button btnChooseFile;
    private Button btnChooseReceivedFile;
    private Button btnSend;
    private EditText txtEmail;
    private EditText txtFilename;
    private TextView lblFilename;
    private ProgressBar progressBar;

    // this is my choosefile. We need an instance, so that we can preload the required data and cache it.
    private chooseReceivedFile myChooseReceivedFile;

    private MyError errorHandler;

    private MyFile file; // the file that will be sent.

    /**
     * This determines whether the file that I need to send is a local file or not.
     * If it is not a local file, it is a file that I received and need to send onwards.
     */
    private boolean isSendFileLocalStorage = true;

    /**
     * This file that I need to send, if `isSendFileLocalStorage` is false
     */
    private dbFile fileToSend;

    /**
     * The user that I received the file from, if `isSendFileLocalStorage` is false
     */
    private dbUser userThatSentFile;

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

    // what happens when the user tries to send a received file.
    private class chooseReceivedFile implements View.OnClickListener {
        class FileUserPair {
            public dbFile file;
            public dbUser user;

            public FileUserPair(dbFile file, dbUser user) {
                this.file = file;
                this.user = user;
            }
        }

        private ArrayList<GetAgreementsUsersFiles.FileUserAgreementTriple> myFiles;
        private boolean hasReceivedFiles = false;
        private AlertDialog loadingDlg = null;
        private AlertDialog listDlg = null;
        public GetAgreementsUsersFiles.FileUserAgreementTriple selectedFile;

        public chooseReceivedFile() {
            myFiles = new ArrayList<>();
        }

        @Override
        public void onClick(View view) {
            makeDialogBox();
        }

        private void makeDialogBox() {
            if (hasReceivedFiles) {
                this.closeDialogBoxes();
                this.makeFileBox();
            } else {
                this.closeDialogBoxes();
                this.makeLoadingBox();
                this.loadDataV2();
            }
        }

        /**
         * Creates a dialog with the files in
         */
        private void makeFileBox() {
            Log.d(LOG_TAG, "Drawing file box " + Arrays.toString(myFiles.toArray()));
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(SendFileActivity.this);
            builder.setTitle("Choose a file to send");
            // add a radio button list

            // first get the name
            Collection<String> col = Collections2.transform(myFiles, new Function<GetAgreementsUsersFiles.FileUserAgreementTriple, String>() {
                @NullableDecl
                @Override
                public String apply(@NullableDecl GetAgreementsUsersFiles.FileUserAgreementTriple input) {
                    if (input == null) return "null";
                    return input.file.getName() + " (from " + input.user.getName() + ")";
                }
            });

            // then we add this as a single choice list
            String[] arr = col.toArray(new String[0]);
            Log.d(LOG_TAG, Arrays.toString(arr));
            AtomicInteger checkedItem = new AtomicInteger();
            builder.setSingleChoiceItems(arr, checkedItem.get(), (dialog, which) -> {
                // user checked an item
                Log.d(LOG_TAG, "Changed an item (" + which + ")");

                checkedItem.set(which);
            });
            // add OK and Cancel buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                // user clicked OK
                Log.d(LOG_TAG, "We chose a received file (" + checkedItem + ")");
                isSendFileLocalStorage = false;
                fileToSend = myFiles.get(checkedItem.get()).file;
                selectedFile = myFiles.get(checkedItem.get());
                updateFileShowUI();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                // we didn't choose anything
                Log.d(LOG_TAG, "Did not choose a received file");
            });
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        /**
         * This loads my received files from firebase, and creates the dialog boxes when the data is received
         */
        private void loadDataV2() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Query q = db.collection("Agreements")
                    .whereEqualTo("userID", userID);
            //                    .whereGreaterThan("validUntil", new Date().getTime())

            chooseReceivedFile self = this;
            ArrayCallback<GetAgreementsUsersFiles.FileUserAgreementTriple> callback = new ArrayCallback<GetAgreementsUsersFiles.FileUserAgreementTriple>() {
                @Override
                public void onSuccess(ArrayList<GetAgreementsUsersFiles.FileUserAgreementTriple> arr, String message) {
                    self.myFiles = arr;
                    self.hasReceivedFiles = true;
                    self.makeDialogBox();
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    // todo
                    Log.d(LOG_TAG, "ERROR when getting agreements " + error);
                }
            };
            GetAgreementsUsersFiles get = new GetAgreementsUsersFiles(q, callback, userID, db);
            get.getData();
        }

        private void closeDialogBoxes() {
            if (loadingDlg != null) loadingDlg.hide();
            if (listDlg != null) listDlg.hide();
        }

        private void makeLoadingBox() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SendFileActivity.this);

            builder.setTitle("Loading...");

            // add OK and Cancel buttons
            final ProgressBar progressBar = new ProgressBar(SendFileActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            builder.setView(progressBar);
            progressBar.setLayoutParams(lp);

            builder.setView(progressBar);
            // create and show the alert dialog

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            loadingDlg = builder.create();
            loadingDlg.show();
        }
    }

    /**
     * This updates our UI, to make sure that the file selected is shown correctly.
     */
    private void updateFileShowUI() {
        // todo
        txtFilename.setVisibility(View.VISIBLE);
        btnSend.setEnabled(true);
        if (isSendFileLocalStorage) {
            // then we show the local file
        } else {
            // show the received file
            GetAgreementsUsersFiles.FileUserAgreementTriple x = myChooseReceivedFile.selectedFile;
            String name;
            if (x != null) {
                name = x.file.getName() + "(From " + x.user.getName() + ")";
            } else {
                name = fileToSend.getName();
            }
            txtFilename.setText(name);
        }
    }

    // what happens when sent
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
                public void onSuccess(Map<String, Object> data, String message) {
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
         * This sends the file
         */
        private void sendV2() {
            // gets the input stream
            InputStream stream;
            int totalSize;
            try {
                stream = getContentResolver().openInputStream(file.getUri());
                totalSize = stream.available();
            } catch (Exception e) {
                errorHandler.displayError("Reading the file failed with message (" + e.getMessage() + ")");
                return;
            }

            Callback cb = new Callback() {
                @Override
                public void onSuccess(Map<String, Object> data, String message) {
                    errorHandler.displaySuccess(message);
                    cleanUp();
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    errorHandler.displayError(error);
                    cleanUp();
                }
            };

            Callback progressCallback = new Callback() {
                // on progress.
                @Override
                public void onSuccess(Map<String, Object> data, String message) {
                    long numBytes = (long) data.get("transferred");
                    Log.d(LOG_TAG, "PROGRESS " + numBytes + " / " + totalSize + " = " + (int) (100 * ((float) numBytes / (float) totalSize)));
                    progressBar.setProgress(
                            (int) (100 * ((float) numBytes / (float) totalSize))
                    );
                }

                @Override // not used
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                }
            };

            // currently logged in user
            user = auth.getCurrentUser();
            if (user == null) {
                // if the user is null, we need to go to the login activity first.
                goToLogin();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            SendLocalFile sender = new SendLocalFile(cb, user.getUid(), userToReceiveID, txtFilename.getText().toString(), stream, FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(), progressCallback);
            sender.send();
        }

        /**
         * This gets called after we successfully get the email. Then we can continue with the process.
         *
         * @param data     The data from Utils.GetEmail. userID: <userID>
         * @param message: Extra info/message
         */
        private void afterGetEmail(Map<String, Object> data, String message) {
            // can now actually send, since we got a proper email.
            userToReceiveID = data.containsKey("userID") ? (String) data.get("userID") : "-1";

            Log.d(LOG_TAG, "Found userID from email: " + userToReceiveID);
            if (true) {
                sendV2();
                return;
            }

        }

        /* Failure handlers, i.e. what happens when stuff fails */

        private void afterFailGetEmail(String error, MyError.ErrorCode errorCode) {
            Log.d(LOG_TAG, "User Email failed with message (" + error + ") and code + (" + errorCode + ")");

            errorHandler.displayError("That user does not exist. Please ask them to sign up first.");
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
        super.onActivityResult(requestCode, resultCode, data);
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
                isSendFileLocalStorage = true;
                updateFileShowUI();
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
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send_file);
        super.onCreate(savedInstanceState);
        myChooseReceivedFile = new chooseReceivedFile();

        setElements();
        setEvents();
        file = new MyFile();
        errorHandler = new MyError(getApplication());
        MyFirebaseMessagingService x = new MyFirebaseMessagingService();
        Log.d(LOG_TAG, String.valueOf(x));
        x.getToken();
        new GoogleApiAvailability().makeGooglePlayServicesAvailable(this);

        setChecked(1);

    }

    /**
     * Sets events
     */
    private void setEvents() {
        btnChooseFile.setOnClickListener(new chooseFile());
        btnSend.setOnClickListener(new sendFile());
        txtFilename.addTextChangedListener(new FilenameChange());

        btnChooseReceivedFile.setOnClickListener(myChooseReceivedFile);
    }

    /**
     * Assigns the Element variables;
     */
    private void setElements() {
        btnChooseFile = findViewById(R.id.sf_btnChooseFile);
        btnChooseReceivedFile = findViewById(R.id.sf_btnChooseFileFromReceived);
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
