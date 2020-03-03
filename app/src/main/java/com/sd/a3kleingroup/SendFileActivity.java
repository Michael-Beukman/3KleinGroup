package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.classes.MyFile;

import org.w3c.dom.Text;

import java.io.File;
import java.nio.file.Path;

import kotlin.NotImplementedError;

public class SendFileActivity extends AppCompatActivity {
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

    private MyFile file; // the file that will be sent.

    //what happens when the file is chosen
    private class chooseFile implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, FILE_REQUEST_CODE);
        }
    }

    private class sendFile implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            // I now need to save the file to storage
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // I now need to upload a file
            // currently logged in.
            FirebaseUser user = auth.getCurrentUser();
            if (user == null){
                // todo
                throw new NotImplementedError();
            }
            String filename = txtFilename.getText().toString();
            String filePathFirebase = user.getUid() + "/" + filename;
            StorageReference fileRef = storageRef.child(filePathFirebase);

            // now upload

            File fileToUpload = file.getFile();
            Uri uriOfFile = Uri.fromFile(fileToUpload);

            UploadTask uploadTask = fileRef.putFile(uriOfFile);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    // todo
                    throw new NotImplementedError();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // todo
                    throw new NotImplementedError();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // progress update
                    // todo
                    throw new NotImplementedError();
                }
            });
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
                String filePath = fileUri.getPath();
                file.setFilepath(filePath);

                // make filename text visible.
                txtFilename.setVisibility(View.VISIBLE);
                txtFilename.setText(filePath);

                Log.d(LOG_TAG, "User chose File: " + filePath);
            } else {
                // todo
                // Failure. Handle Error
                Log.e(LOG_TAG, "File Error with code " + resultCode);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        setElements();
        setEvents();
        file = new MyFile();
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

        txtFilename.setVisibility(View.INVISIBLE);
    }
}
