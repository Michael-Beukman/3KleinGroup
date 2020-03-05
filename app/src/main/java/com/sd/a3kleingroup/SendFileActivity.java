package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sd.a3kleingroup.classes.MyFile;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import kotlin.NotImplementedError;

import com.google.cloud.datastore.Datastore;

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
    private ProgressBar progressBar;

    private MyFile file; // the file that will be sent.

    //what happens when the file is chosen
    private class chooseFile implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            // use pick instead to get a file URI.
//            Intent chooseFile = new Intent(Intent.ACTION_PICK);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, FILE_REQUEST_CODE);
        }
    }

    private class sendFile implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // I now need to save the file to storage
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // I now need to upload a file
            // currently logged in.
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                // todo
                throw new NotImplementedError();
            }
            String filename = txtFilename.getText().toString();
            String filePathFirebase = user.getUid() + "/" + filename;
            StorageReference fileRef = storageRef.child(filePathFirebase);

            // now upload

            File fileToUpload = file.getFile();
            Log.d(LOG_TAG, "File exists" + fileToUpload.exists());

            InputStream stream;

            int totalSize;
            try {
                stream = getContentResolver().openInputStream(file.getUri());
                totalSize = stream.available();
            } catch (IOException e) {
                throw new NotImplementedError();
            }

            UploadTask uploadTask = fileRef.putStream(stream);
            progressBar.setVisibility(View.VISIBLE);
            // Register observers to listen for when the download is done or if it fails
            int finalTotalSize = totalSize;
            int finalTotalSize1 = totalSize;
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
                progressBar.setVisibility(View.INVISIBLE);
                Toast t = Toast.makeText(getApplicationContext(), "An Error occured (" + exception.getMessage() + ")", Toast.LENGTH_LONG);
                t.show();
                Log.d(LOG_TAG, "Failed");
                // todo
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast t = Toast.makeText(getApplicationContext(), "Successfully uploaded file", Toast.LENGTH_LONG);
                t.show();
                Log.d(LOG_TAG, "Success");
                progressBar.setVisibility(View.INVISIBLE);

                // =========== \\
                // now we need to update DB
                // =========== \\
                Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

                // get user's entity
                Query<Entity> query = Query.newEntityQueryBuilder()
                        .setKind("User")
                        .setFilter(StructuredQuery.CompositeFilter.and(
                                StructuredQuery.PropertyFilter.eq("id", user.getUid()), StructuredQuery.PropertyFilter.ge("priority", 4)))
                        .setOrderBy(StructuredQuery.OrderBy.desc("priority"))
                        .build();

//                Entity userThatSent = datastore.get();
                QueryResults<Entity> users = datastore.run(query);
                Entity userThatSent = users.next();


                // create a file entity


                Key taskKey = datastore.newKeyFactory()
                        .setKind("File")
                        .newKey("sampleTask");
                // actually create entity
                Entity task = Entity.newBuilder(taskKey)
                        .set("filepath", filePathFirebase)
                        .set("name", filename)
                        .set("userID", user.getUid())
                        .build();

                // upsert, i.e. insert if not exists else update
                Entity fileKey = datastore.put(task);
                String fileID = fileKey.getKey().getName();
                Log.d(LOG_TAG, "FILE ID: " + fileID);
                // update agreements
                // todo fix. This is ust a hack, need to actually get the userID
                String otherUserID = "vDMdClPofyU2SIWXn6BCJx5qZxh2";

                Key task2Key = datastore.newKeyFactory()
                        .setKind("Agreement")
                        .newKey("temp");

                Entity agreement = Entity.newBuilder(task2Key)
                                    .set("FileID", fileID)
                                    .set("UserID", otherUserID)
                                    .build();

                datastore.put(agreement);


            }).addOnProgressListener(taskSnapshot -> {
                // progress update
                // just set the progress to be transferred/total

                Log.d(LOG_TAG, "PROGRESS " + taskSnapshot.getBytesTransferred() + " / " + finalTotalSize + " = " + (int) (100 * ((float) taskSnapshot.getBytesTransferred() / (float) finalTotalSize1)));
                progressBar.setProgress(
                        (int) (100 * ((float) taskSnapshot.getBytesTransferred() / (float) finalTotalSize))
                );
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
                Log.d(LOG_TAG, "URI: " + fileUri + " PATH: " + fileUri.getPath() + " Exists + " + new File(fileUri.getPath()).exists());

                String filePath = fileUri.getPath();
                file.setFilepath(filePath);
                file.setUri(fileUri);

                // make filename text visible.
                txtFilename.setVisibility(View.VISIBLE);
                txtFilename.setText(filePath);
                // + " " + new File(getPath(fileUri)).exists()
                Log.d(LOG_TAG, "User chose File: " + filePath + " get path " + getPath(fileUri));
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
        progressBar = findViewById(R.id.sf_prgbProgress);

        // change default visibility
        txtFilename.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

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
