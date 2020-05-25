package com.sd.a3kleingroup;

import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.SingleSentFile;
import com.sd.a3kleingroup.classes.db.dbAgreement;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.Map;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.util.EnumSet.allOf;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnit4.class)
public class MySentFilesTest {
    final int TIME_TO_SLEEP=2000;
    @Rule
    public ActivityScenarioRule<MySentFiles> activityScenarioRule
            = new ActivityScenarioRule<>(MySentFiles.class);

    @Before
    public void setUp() throws Exception {
        // Log IN first
         Log.d("REEEEEE", "HELLO1 " + FirebaseAuth.getInstance().getCurrentUser());

        AuthCredential authCredential = GoogleAuthProvider.getCredential("eyJhbGciOiJSUzI1NiIsImtpZCI6ImE1NDFkNmVmMDIyZDc3YTIzMThmN2RkNjU3ZjI3NzkzMjAzYmVkNGEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI3ODU2NzM4NTAzOTMtbDhldjFqdHRmNDByaXM3Y2Y1aDZuMW4ya29yYXZzYTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI3ODU2NzM4NTAzOTMtMHBxaWY3dWo4bWNlNW1kZjBqczV2cWU3NHA1c2xldWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTU4Njk1MzUxMDQ5MzE4NDE3MTYiLCJlbWFpbCI6Im1pZ2h0eW1hbjc3MEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Im1pZ2h0eSBtYW4iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tLy1BUWh0SXh4VzhQMC9BQUFBQUFBQUFBSS9BQUFBQUFBQUFBQS9BS0YwNW5ERFFNZmoyS3F1cGI1c0ZuVXhZZjhYZDFLTjFnL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJtaWdodHkiLCJmYW1pbHlfbmFtZSI6Im1hbiIsImxvY2FsZSI6ImVuLUdCIiwiaWF0IjoxNTg0NDQxNjI1LCJleHAiOjE1ODQ0NDUyMjV9.ZlOafZtimOrVINgjlS0uh8LMrX_3R0l1rq55b9nyIL7GIQu8luF_REHUrBjAxmJwEG2KqfOEJ4DMdMFbWvA_jLyWgNqLt2JL4BumGn53KRhGIVHwSzW_eNQ_0p5X4hJ_o9lftIDQ6B7sdX4kKuB0ihJ8h0mDBt-RRy6JxHv2WdxdJYrtQcGRSxB5HdLi2I5CAvSoxJ_wCDchSmkqtuuZTmn_GQIep-nHvfKJwWZP41oaIaFoeNqstHEkIYh860ne93jA90aRO7LDv7ZPaMXdb89zHG9R5ig49XO6c5YclracuhX4m0xCakdUG4BiTEyizMgYE2-rwdBhn6tTK8kAFA", null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(y->{
           Log.d("REEEEEE", "Logged in");
        });
        Thread.sleep(TIME_TO_SLEEP);
    }

    @Test
    public void onCreate() {
        assertEquals(1,1);
    }

    @Test
    public void testGetAsync() throws InterruptedException {
        // Correct, test twice
        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.getAsync(activity.USER_COLLECTION_NAME, "25MsMCqDk0TxwoQB5IjwExZnJHf2", new Callback() {
                @Override
                public void onSuccess(Map<String, Object> data, String message) {

                    assertEquals(data.get("email"), "g.axelrod1@gmail.com");
                    assertEquals(data.get("name"), "Guy Axelrod");
                    assertNotNull(data.get("notificationToken"));
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    assertEquals(1, 0); //fail();
                }
            });
        });
        activityScenarioRule.getScenario().onActivity(activity -> activity.getAsync(activity.FILE_COLLECTION_NAME, "lgOW6jYP1hXPmHJKE4i2", new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                assertEquals(data.get("filename"), "/document/acc=1;doc=15");
                assertEquals(data.get("filepath"), "MxTtBm9zkTaesi86UH5uaqGKvlA2/_document_acc=1;doc=15");
                assertEquals(data.get("storageURL"), "https://firebasestorage.googleapis.com/v0/b/kleingroupv2.appspot.com/o/MxTtBm9zkTaesi86UH5uaqGKvlA2%2F_document_acc%3D1%3Bdoc%3D15?alt=media&token=112d10a8-42f1-4321-ba39-154b1d177cab");
                assertEquals(data.get("userID"), "En8fRBqxPiZ13HvOabUx7uOXN2T2");
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                assertEquals(1,0);
            }
        }));

        activityScenarioRule.getScenario().onActivity(activity -> activity.getAsync(activity.USER_COLLECTION_NAME, "badID", new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                assertEquals(1,0);
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                assertEquals("No data", error);
                assertEquals(MyError.ErrorCode.NOT_FOUND, errorCode);
            }
        }));

        Thread.sleep(TIME_TO_SLEEP);
    }

    @Test
    public void changePermissionsIndef() throws InterruptedException {
        String docID = "k7XccP4XYnAHkD638sT6";
        Date d = dbAgreement.getDate20YearsInfuture();
        SingleSentFile testFile = new SingleSentFile("ZLCxcsanWFqIqYxnaeoD",
                "MxTtBm9zkTaesi86UH5uaqGKvlA2", d,
                    "MxTtBm9zkTaesi86UH5uaqGKvlA2",docID);
        activityScenarioRule.getScenario().onActivity(activity -> {

            // now change permissions
            activity.approveIndefinitely(testFile);
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final boolean[] first = {true};
            db.collection("Agreements").document(docID).addSnapshotListener((documentSnapshot, e) -> {
                assertTrue(((Timestamp)documentSnapshot.getData().get("validUntil")).toDate().after(d));
            });

        });

        Thread.sleep(TIME_TO_SLEEP);

    }

    @Test
    public void changePermissionsTomorrow() throws InterruptedException {
        String docID = "k7XccP4XYnAHkD638sT6";
        Date d = dbAgreement.getDateTomorrow();
        SingleSentFile testFile = new SingleSentFile("ZLCxcsanWFqIqYxnaeoD",
                "MxTtBm9zkTaesi86UH5uaqGKvlA2", d,
                "MxTtBm9zkTaesi86UH5uaqGKvlA2",docID);
        activityScenarioRule.getScenario().onActivity(activity -> {

            // now change permissions
            activity.approveTilTomorrow(testFile);
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final boolean[] first = {true};
            db.collection("Agreements").document(docID).addSnapshotListener((documentSnapshot, e) -> {
                if (first[0])
                    first[0] = false;
                else{
                    // this is the second change, i.e. after changing
                    assertTrue(((Timestamp)documentSnapshot.getData().get("validUntil")).toDate().after(d));
                }
            });


        });

        Thread.sleep(TIME_TO_SLEEP);

    }

    @Test
    public void changePermissionsRevoke() throws InterruptedException {
        String docID = "JNHLV5eQ9nCYEfvonBQ9";
        Date d = dbAgreement.getDateInPast();
        SingleSentFile testFile = new SingleSentFile("lgOW6jYP1hXPmHJKE4i2",
                "En8fRBqxPiZ13HvOabUx7uOXN2T2", d,
                "MxTtBm9zkTaesi86UH5uaqGKvlA2",docID);
        activityScenarioRule.getScenario().onActivity(activity -> {


            // now change permissions
            activity.revoke(testFile);
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Agreements").document(docID).addSnapshotListener((documentSnapshot, e) -> {
                Log.d("LOG_MY_TEST_LOG", e!=null ? e.getMessage(): " no exceptions ");
                Log.d("LOG_MY_TEST_LOG", String.valueOf(((Timestamp)documentSnapshot.getData().get("validUntil")).toDate()));
                assertTrue(((Timestamp)documentSnapshot.getData().get("validUntil")).toDate().before(new Date()));
            });
        });


        Thread.sleep(TIME_TO_SLEEP);

    }

    @Test
    public void testUI(){
        activityScenarioRule.getScenario().onActivity(a ->{
            View v= new View(a.getApplicationContext()); // a.findViewById(R.id.imgBtn_Hamburger);
            Log.d("MY_TEST", "" + v);
            a.showPopup(v);
        });
    }

    @Test
    public void binds(){
        activityScenarioRule.getScenario().onActivity(a -> {
            Query query = FirebaseFirestore.getInstance().collection("Agreements").whereEqualTo("ownerID", "eR1HwsAKAifArcER3CACMIzMxkF3");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e == null) {
                    MySentFiles.MyRecyclerViewAdapter tmp = a.new MyRecyclerViewAdapter(queryDocumentSnapshots);
                }
            });
        });
    }
}
