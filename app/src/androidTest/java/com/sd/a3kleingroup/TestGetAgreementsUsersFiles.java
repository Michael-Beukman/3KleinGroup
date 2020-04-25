package com.sd.a3kleingroup;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.callbacks.ArrayCallback;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.querying.GetAgreementsUsersFiles;
import com.sd.a3kleingroup.classes.querying.GetAgreementsUsersFiles.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

@RunWith(JUnit4.class)
public class TestGetAgreementsUsersFiles {
    int TIME_TO_SLEEP = 2000;
    ArrayList<FileUserAgreementTriple> testData;

    @Before
    public void setUp() throws Exception {
        // Log IN first
        Log.d("REEEEEE", "HELLO1 " + FirebaseAuth.getInstance().getCurrentUser());
        AuthCredential authCredential = GoogleAuthProvider.getCredential("eyJhbGciOiJSUzI1NiIsImtpZCI6ImE1NDFkNmVmMDIyZDc3YTIzMThmN2RkNjU3ZjI3NzkzMjAzYmVkNGEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI3ODU2NzM4NTAzOTMtbDhldjFqdHRmNDByaXM3Y2Y1aDZuMW4ya29yYXZzYTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI3ODU2NzM4NTAzOTMtMHBxaWY3dWo4bWNlNW1kZjBqczV2cWU3NHA1c2xldWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTU4Njk1MzUxMDQ5MzE4NDE3MTYiLCJlbWFpbCI6Im1pZ2h0eW1hbjc3MEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Im1pZ2h0eSBtYW4iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1BUWh0SXh4VzhQMC9BQUFBQUFBQUFBSS9BQUFBQUFBQUFBQS9BS0YwNW5ERFFNZmoyS3F1cGI1c0ZuVXhZZjhYZDFLTjFnL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJtaWdodHkiLCJmYW1pbHlfbmFtZSI6Im1hbiIsImxvY2FsZSI6ImVuLUdCIiwiaWF0IjoxNTg0NDQxNjI1LCJleHAiOjE1ODQ0NDUyMjV9.ZlOafZtimOrVINgjlS0uh8LMrX_3R0l1rq55b9nyIL7GIQu8luF_REHUrBjAxmJwEG2KqfOEJ4DMdMFbWvA_jLyWgNqLt2JL4BumGn53KRhGIVHwSzW_eNQ_0p5X4hJ_o9lftIDQ6B7sdX4kKuB0ihJ8h0mDBt-RRy6JxHv2WdxdJYrtQcGRSxB5HdLi2I5CAvSoxJ_wCDchSmkqtuuZTmn_GQIep-nHvfKJwWZP41oaIaFoeNqstHEkIYh860ne93jA90aRO7LDv7ZPaMXdb89zHG9R5ig49XO6c5YclracuhX4m0xCakdUG4BiTEyizMgYE2-rwdBhn6tTK8kAFA", null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(y -> {
            Log.d("REEEEEE", "Logged in");
        });


        insertTestData();
    }

    @After
    public void tearDown(){
    }


    @Test
    public void testEmpty() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query empty = db.collection("Agreements")
                .whereEqualTo("non_existant_field", "-1");

        GetAgreementsUsersFiles agr = new GetAgreementsUsersFiles(empty, new ArrayCallback<FileUserAgreementTriple>() {
            @Override
            public void onSuccess(ArrayList<GetAgreementsUsersFiles.FileUserAgreementTriple> arr, String message) {
                assertEquals(0, arr.size());
                Log.d("MY_TESTING", "In here");
            }
            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                assertTrue(false);
            }
        }, "userID", db);

        Thread.sleep(TIME_TO_SLEEP);
        agr.getData();
    }

    @Test
    public void testNonEmpty() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query nonEmpty = db.collection("Agreements")
                .whereEqualTo("fileID", "TEST_fileID1")
                .whereEqualTo("ownerID", "TEST_userID1");

        GetAgreementsUsersFiles agr = new GetAgreementsUsersFiles(nonEmpty, new ArrayCallback<FileUserAgreementTriple>() {
            @Override
            public void onSuccess(ArrayList<GetAgreementsUsersFiles.FileUserAgreementTriple> arr, String message) {
                assertEquals(1, arr.size());
//                assertEquals(testData, arr);
                Log.d("MY_TESTING", "In here with size " +arr.size());
                System.out.println("MY_TESTING " +  "In here with size " +arr.size() + Arrays.toString(arr.toArray()));

            }
            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                fail("Shouldn't be here");
            }
            // userID is me, i.e. current user, not the owner
        }, "TEST_userID2", db);

        Thread.sleep(TIME_TO_SLEEP * 3);
        agr.getData();
    }



    private void insertTestData() throws InterruptedException {
        // inserts data so that we can see if it works
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        testData = new ArrayList<>();
        testData.add(new FileUserAgreementTriple(
                new dbFile("path1", "name1", "TEST_userID1", "url1", "key1"),
                new dbAgreement("TEST_fileID1", "TEST_userID2", new Date(), "TEST_userID1"),
                new dbUser("email1", "username1")
        ));
        for (FileUserAgreementTriple t: testData){
            db.collection("Agreements").document("TEST_agreementID1").set(t.agreement.getHashmap());

            db.collection("Users").document("TEST_userID1").set(t.user.getHashmap());

            db.collection("Files").document("TEST_fileID1").set(t.file.getHashmap());
        }

        Thread.sleep(TIME_TO_SLEEP);
    }
}
