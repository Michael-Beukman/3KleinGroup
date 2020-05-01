package com.sd.a3kleingroup;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.Sending.SendLocalFile;
import com.sd.a3kleingroup.classes.callbacks.NullCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class SendLocalFilesTest {

    private String LOG_TAG = "MY_TEST_SendLocalFilesTest";
    boolean doTearDown = false;

    @Before
    public void setUp() throws Exception {
        // Log IN first
        Log.d("REEEEEE", "HELLO1 " + FirebaseAuth.getInstance().getCurrentUser());
        FirebaseAuth.getInstance().signInWithEmailAndPassword("abc@gmail.com", "abc123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
        Thread.sleep(2000);
    }

    @Test
    public void testFailings() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String err = "error msg";
        boolean databaseError = false; // is the error a db error (this is just to check the error message)
        boolean[] finalDatabaseError = new boolean[]{false};
        boolean[] finalDatabaseError1 = finalDatabaseError;
        SendLocalFile object = new SendLocalFile(new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                fail("Should not call success here");
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                if (finalDatabaseError1[0]) {
                    assertEquals("An Error occurred while updating database (" + err + ")", error);
                } else
                    assertEquals("An Error occured (" + err + ")", error);

            }
        }, "sending", "receiving", "filename", null, null, null, null);

        Method privateStringMethod = SendLocalFile.class.
                getDeclaredMethod("afterFailUploadFile", new Class[]{Exception.class});
        privateStringMethod.setAccessible(true);
        privateStringMethod.invoke(object, new Exception("error msg"));


        finalDatabaseError1[0] = true;
        privateStringMethod = SendLocalFile.class.
                getDeclaredMethod("afterFailUpdateDB", new Class[]{Exception.class});
        privateStringMethod.setAccessible(true);
        privateStringMethod.invoke(object, new Exception("error msg"));
    }

    @Test
    public void testSending() throws IOException, InterruptedException {
        doTearDown = true;
        // first create a file.

        File file = File.createTempFile("test", ".txt"); // new File(Environment.getDataDirectory() + "/" + File.separator + "test.txt");
//        file.createNewFile();
        byte[] data1 = {1, 1, 0, 0};
        //write the bytes in file
        if (file.exists()) {
            OutputStream fo = new FileOutputStream(file);
            fo.write(data1);
            fo.close();
            System.out.println("file created: " + file);
        }
        byte[] initialArray = {0, 1, 2, 3, 4, 5};
        InputStream toSendStream = new ByteArrayInputStream(initialArray);

        // now we test the uploading
        SendLocalFile sender = new SendLocalFile(new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                assertEquals("Database updated successfully", message);
                // now check if the file exists in the db.
                FirebaseFirestore.getInstance()
                        .collection("Files")
                        .whereEqualTo("filename", "tmpTestFileName")
                        .get()
                        .addOnCompleteListener(thing -> {
                           assertTrue(thing.isSuccessful());
                           assertTrue(thing.getResult().size() > 0);
                        });

            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                Log.d(LOG_TAG, error + " error");
                fail("We should not fail here!");
            }
        },
                "25MsMCqDk0TxwoQB5IjwExZnJHf2", // Guy,
                "MxTtBm9zkTaesi86UH5uaqGKvlA2", // Mike
                "tmpTestFileName",
                toSendStream, // what to send
                FirebaseFirestore.getInstance(),
                FirebaseStorage.getInstance(),
                new NullCallback()
        );

        sender.send();
        Thread.sleep(3000);
        //deleting the file
        file.delete();
        System.out.println("file deleted");
    }

    @After
    public void tearDown() throws InterruptedException {
        if (!doTearDown) return;
        Log.d(LOG_TAG, "Starting teardown");
        // clean up the agreement and file DB.
        ArrayList<String> idsToDelete = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("Files")
                .whereEqualTo("filename", "tmpTestFileName")
                .get().addOnSuccessListener(documentSnapshots -> {
            if (documentSnapshots.size() > 0) {
                for (DocumentSnapshot d : documentSnapshots.getDocuments()) {
                    String id = d.getId();
                    Log.d(LOG_TAG, "starting to delete " + id);
                    idsToDelete.add(id);
                    FirebaseFirestore.getInstance()
                            .collection("Files").document(id).delete().addOnCompleteListener(task ->
                            Log.d(LOG_TAG, "Completed deleting success = " + task.isSuccessful()));
                }
            } else {
                Log.d(LOG_TAG, "There were 0 files to delete");

            }
        });

        Thread.sleep(3000);
        if (idsToDelete.size() == 0){
            return;
        }
        // now delete agreements
        FirebaseFirestore.getInstance()
                .collection("Agreements")
                .whereIn("fileID", idsToDelete)
                .get()
                .addOnSuccessListener(task -> {
                   for (DocumentSnapshot d: task.getDocuments()){
                       String id = d.getId();
                       Log.d(LOG_TAG, "starting to delete the agreement " + id);
                       FirebaseFirestore.getInstance()
                               .collection("Agreements")
                               .document(id)
                               .delete();
                   }
                });

        Thread.sleep(4000);
    }
}
