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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnit4.class)
public class MySentFilesTest {
    @Rule
    public ActivityScenarioRule<MySentFiles> activityScenarioRule
            = new ActivityScenarioRule<>(MySentFiles.class);
    @Rule public ActivityScenarioRule<LoginActivity> loginRule
            = new ActivityScenarioRule<>(LoginActivity.class);
    @Before
    public void setUp() throws Exception {
        loginRule.getScenario().onActivity(activity -> {
            activity.signIn();
            Log.d("REEEEEE", "HELLO " + FirebaseAuth.getInstance().getCurrentUser());
        });
        Log.d("REEEEEE", "HELLO1 " + FirebaseAuth.getInstance().getCurrentUser());
    }

    @Test
    public void onCreate() {
        assertEquals(1,1);
    }

    @Test
    public void testGetAsync(){
        // Correct, test twice
        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.getAsync(activity.USER_COLLECTION_NAME, "25MsMCqDk0TxwoQB5IjwExZnJHf2", new Callback() {
                @Override
                public void onSuccess(Map<String, Object> data, String message) {
                    assertEquals(data.get("email"), "g.axelrod1@gmail.com");
                    assertEquals(data.get("name"), "Guy Axelrod");
                    assertNull(data.get("notificationToken"));
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    assertEquals(1, 0);
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
    }
    /*
    @Test
    public void changePermissionsIndef(){
        String docID = "k7XccP4XYnAHkD638sT6";
        Date d = dbAgreement.getDate20YearsInfuture();
        SingleSentFile testFile = new SingleSentFile("ZLCxcsanWFqIqYxnaeoD",
                "MxTtBm9zkTaesi86UH5uaqGKvlA2", d,
                    "MxTtBm9zkTaesi86UH5uaqGKvlA2",docID);
        activityScenarioRule.getScenario().onActivity(activity -> {

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

            // now change permissions
            activity.approveIndefinitely(testFile);
        });
    }
    */
    /*
    @Test
    public void changePermissionsTomorrow(){
        String docID = "k7XccP4XYnAHkD638sT6";
        Date d = dbAgreement.getDateTomorrow();
        SingleSentFile testFile = new SingleSentFile("ZLCxcsanWFqIqYxnaeoD",
                "MxTtBm9zkTaesi86UH5uaqGKvlA2", d,
                "MxTtBm9zkTaesi86UH5uaqGKvlA2",docID);
        activityScenarioRule.getScenario().onActivity(activity -> {

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

            // now change permissions
            activity.approveTilTomorrow(testFile);
        });
    }

    @Test
    public void changePermissionsRevoke(){
        String docID = "k7XccP4XYnAHkD638sT6";
        Date d = dbAgreement.getDateInPast();
        SingleSentFile testFile = new SingleSentFile("ZLCxcsanWFqIqYxnaeoD",
                "MxTtBm9zkTaesi86UH5uaqGKvlA2", d,
                "MxTtBm9zkTaesi86UH5uaqGKvlA2",docID);
        activityScenarioRule.getScenario().onActivity(activity -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final boolean[] first = {true};
            db.collection("Agreements").document(docID).addSnapshotListener((documentSnapshot, e) -> {
                if (first[0])
                    first[0] = false;
                else{
                    // this is the second change, i.e. after changing
                    assertTrue(((Timestamp)documentSnapshot.getData().get("validUntil")).toDate().before(new Date()));
                }
            });

            // now change permissions
            activity.revoke(testFile);
        });
    }
*/
}
