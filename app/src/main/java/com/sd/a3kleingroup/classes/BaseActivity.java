package com.sd.a3kleingroup.classes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.FriendListActivity;
import com.sd.a3kleingroup.LoginActivity;
import com.sd.a3kleingroup.MySentFiles;
import com.sd.a3kleingroup.PublicFileManagerActivity;
import com.sd.a3kleingroup.PublicFileReceiveActivity;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.ReceiveFilesActivity;
import com.sd.a3kleingroup.SendFileActivity;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.encryption.AESEncryption;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {
//    @Override
    protected String LOG_TAG;
    protected BottomNavigationView nav;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOG_MyBaseActivity1", "re");

        // check if logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            goToLogin();
            return;
        }

        MyFirebaseMessagingService x = new MyFirebaseMessagingService();
        Log.d("LOG_MyBaseActivity", String.valueOf(x));
        x.getToken();
        doNavigation();
    }

    public void goToLogin(){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    protected void doNavigation(){
        nav = findViewById(R.id.bottom_navigation);
        Log.d("LOG_MyBaseActivity1", "NAV IS " + nav);
        if (nav != null){
            nav.setOnNavigationItemSelectedListener(item -> {
                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        goToHome();
//                        return true;
                    case R.id.navigation_mysentfiles:
                        goToMySentFiles();
                        return true;
                    case R.id.navigation_recvFiles:
                        goToRecvFiles();
                        return true;
                    case R.id.navigation_sendfiles:
                        goToSendFiles();
                        return true;
//                    case R.id.navigation_message:
//                        goToMessages();
//                        return true;
                    case R.id.navigation_public_receive_files:
                        goToFriendsList();
                        return true;
                    case R.id.navigation_public_manage_files:
                        goToPublicManageFiles();
                        return true;
                }
                return false;
            });
        }
    }

    private void goToMessages() {
        // todo
    }

    private void goToSendFiles() {
        Intent I = new Intent(getApplicationContext(), SendFileActivity.class);
        startActivity(I);
    }

    private void goToRecvFiles() {
        Intent I = new Intent(getApplicationContext(), ReceiveFilesActivity.class);
        startActivity(I);
    }

    private void goToMySentFiles() {
        Intent I = new Intent(getApplicationContext(), MySentFiles.class);
        startActivity(I);

    }

    private void goToHome() {
        //todo
    }

    private void goToPublicManageFiles(){
        Intent I = new Intent(getApplicationContext(), PublicFileManagerActivity.class);
        startActivity(I);
    }

    private void goToFriendsList(){
        Intent I = new Intent(getApplicationContext(), FriendListActivity.class);
        startActivity(I);
    }

    /**
     * Sets nav item i checked if nav is not null.
     * @param item
     */
    protected void setChecked(int item){
        if (nav != null)
            nav.getMenu().getItem(item).setChecked(true);
    }



    public void getAsync(String collectionName, String docID, Callback cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName).document(docID).get().addOnSuccessListener(documentSnapshot -> {
            Log.d(LOG_TAG, "Got data " + documentSnapshot.getData() + " " + collectionName + " " + docID);
            if (documentSnapshot.getData() == null) {
                cb.onFailure("No data", MyError.ErrorCode.NOT_FOUND);
                System.out.println("NEEE");

            } else {
                cb.onSuccess(documentSnapshot.getData(), docID);
                System.out.println("NEEE");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cb.onFailure(e.getMessage(), MyError.ErrorCode.TASK_FAILED);
                System.out.println("NEEE");
            }
        });
    }


    /**
     * Logs in with a temporary user and runs callback on that.
     * @param cb
     */
    public void loginTempUser(Callback cb) {
        // Log IN first
        Log.d("REEEEEE", "HELLO1 " + FirebaseAuth.getInstance().getCurrentUser());
        FirebaseAuth.getInstance().signInWithEmailAndPassword("abc@gmail.com", "abc123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            cb.onSuccess(null, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                            cb.onFailure("signInWithEmail:failure", MyError.ErrorCode.TASK_FAILED);

                        }
                    }
                });
    }


    /**
     * This downloads a file from this filemodel.
     * @param file
     */
    public void downloadFile(dbFile file) {
        if (file == null){
            Log.d(LOG_TAG, "ERROR IN DOWNLOAD FILE. File or file.getFile == null!!!");
            new MyError(this).displayError("We couldn't download a file, because there was an error");
            return;
        }
        //TODO: This is broken now even just with pdf even though didn't really change anything. Getting error java.io.IOException: javax.crypto.BadPaddingException: pad block corrupted
        //Does this mean the key is getting corrupted?
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(file.getFilepath());
        fileRef.getMetadata().addOnSuccessListener(storageMetadata -> {
//            String mime = storageMetadata.getContentType();
//            Log.d(LOG_TAG, mime);
            //We're just gonna assume it's a pdf or an image

            final long ONE_MEGABYTE = 1024 * 1024 * 10;
            fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                //bytes is of type byte[]
                AESEncryption decryptor = new AESEncryption(file.getEncryptionKey());
                InputStream stream = decryptor.decrypt(new ByteArrayInputStream(bytes));
                try {
                    //Create temp file to store file so that external apps can be used to open it.
                    File tmpFile = File.createTempFile("tmp", ".pdf");
                    //OutputStream outputStream = new FileOutputStream(tmpFile);
                    java.nio.file.Files.copy(
                            stream,
                            tmpFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    //IOUtils.copyStream(stream, outputStream);

                    //Open file with external app
                    //For why FileProvider used, see: https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
                    Uri path = FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".provider", tmpFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, file.getFileType());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                    tmpFile.deleteOnExit();
                    //outputStream.flush();
                    //outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
}
