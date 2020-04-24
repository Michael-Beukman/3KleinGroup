package com.sd.a3kleingroup;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.Sending.SendReceivedFile;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.querying.GetAgreementsUsersFiles;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class TestSendReceivedFile {
    private static final String TAG = "MY_TestSendReceivedFile";

    @Before
    public void setUp() throws Exception {
        // Log IN first
        AuthCredential authCredential = GoogleAuthProvider.getCredential("eyJhbGciOiJSUzI1NiIsImtpZCI6ImE1NDFkNmVmMDIyZDc3YTIzMThmN2RkNjU3ZjI3NzkzMjAzYmVkNGEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI3ODU2NzM4NTAzOTMtbDhldjFqdHRmNDByaXM3Y2Y1aDZuMW4ya29yYXZzYTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI3ODU2NzM4NTAzOTMtMHBxaWY3dWo4bWNlNW1kZjBqczV2cWU3NHA1c2xldWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTU4Njk1MzUxMDQ5MzE4NDE3MTYiLCJlbWFpbCI6Im1pZ2h0eW1hbjc3MEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Im1pZ2h0eSBtYW4iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1BUWh0SXh4VzhQMC9BQUFBQUFBQUFBSS9BQUFBQUFBQUFBQS9BS0YwNW5ERFFNZmoyS3F1cGI1c0ZuVXhZZjhYZDFLTjFnL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJtaWdodHkiLCJmYW1pbHlfbmFtZSI6Im1hbiIsImxvY2FsZSI6ImVuLUdCIiwiaWF0IjoxNTg0NDQxNjI1LCJleHAiOjE1ODQ0NDUyMjV9.ZlOafZtimOrVINgjlS0uh8LMrX_3R0l1rq55b9nyIL7GIQu8luF_REHUrBjAxmJwEG2KqfOEJ4DMdMFbWvA_jLyWgNqLt2JL4BumGn53KRhGIVHwSzW_eNQ_0p5X4hJ_o9lftIDQ6B7sdX4kKuB0ihJ8h0mDBt-RRy6JxHv2WdxdJYrtQcGRSxB5HdLi2I5CAvSoxJ_wCDchSmkqtuuZTmn_GQIep-nHvfKJwWZP41oaIaFoeNqstHEkIYh860ne93jA90aRO7LDv7ZPaMXdb89zHG9R5ig49XO6c5YclracuhX4m0xCakdUG4BiTEyizMgYE2-rwdBhn6tTK8kAFA", null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(y -> {
            Log.d("REEEEEE", "Logged in");
        });
    }
    @Test
    public void testStuff() throws InterruptedException {
        dbAgreement a = new dbAgreement("TEST_fileID1", "25MsMCqDk0TxwoQB5IjwExZnJHf2", "25MsMCqDk0TxwoQB5IjwExZnJHf2");
        AtomicBoolean val = new AtomicBoolean(false);
        Callback c = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                assertEquals(new HashMap<String, Object>(), data);
                assertEquals(message, "Success");

                // now actually check if the data is there
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Agreements")
                        .whereEqualTo("userID", "TEST_userID1")
                        .whereEqualTo("fileID", "TEST_fileID1")
                        .get().addOnCompleteListener(task -> {
                    assertTrue(task.isSuccessful());
                    assertTrue(task.getResult().size() > 0);
                    val.set(true);
                });
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                fail("Should not fail");
            }
        };
        GetAgreementsUsersFiles.FileUserAgreementTriple tmp = new GetAgreementsUsersFiles.FileUserAgreementTriple(
                new dbFile("unknown", "unknown", "unknown", "unknown", "unknown"),
                a,
                new dbUser("unknown", "unknown")
        );

        SendReceivedFile f = new SendReceivedFile(c, tmp, "TEST_userID1", FirebaseFirestore.getInstance());
        f.send();
        Thread.sleep(6000);


        assertTrue(val.get());
    }

    @After
    public void tearDown() throws InterruptedException {
        // now delete data
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Agreements")
                .whereEqualTo("userID", "TEST_userID1")
                .whereEqualTo("fileID", "TEST_fileID1")
                .get().addOnSuccessListener(task -> {
            Log.d(TAG, "Got data + size= " + task.size());
            if (task.size() > 0) {
                db.collection("Agreements").document(task.getDocuments().get(0).getId())
                        .delete().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Log.d(TAG, "Successfully deleted.");
                    } else {
                        Log.d(TAG, "Failed to delete " + task2.getException().getMessage());
                    }
                });
            }
        });

        Thread.sleep(5000);
    }
}
