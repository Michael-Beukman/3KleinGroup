package com.sd.a3kleingroup;

import android.app.Activity;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

//@RunWith(RobolectricTestRunner.class)
////@RunWith(JUnit4.class)
//@Config(sdk = 27)
public class RoboElectricTest {
//    ActivityController<MySentFiles> controller;
//    MySentFiles act;
//
//    @Before
//    public void setUp(){
//        controller = Robolectric.buildActivity(MySentFiles.class);
//        act = controller.get();
//        FirebaseApp.initializeApp(act.getApplicationContext());
////        FirebaseApp.initializeApp(act);
//    }
//
////    @Test
//    public void testGetAsync(){
//        // Correct, test twice
//        act.getAsync(act.USER_COLLECTION_NAME, "25MsMCqDk0TxwoQB5IjwExZnJHf2", new Callback() {
//                @Override
//                public void onSuccess(Map<String, Object> data, String message) {
//                    assertEquals(data.get("email"), "g.axelrod1@gmail.com");
//                    assertEquals(data.get("name"), "Guy Axelrod");
//                    assertNull(data.get("notificationToken"));
//                }
//
//                @Override
//                public void onFailure(String error, MyError.ErrorCode errorCode) {
//                    assertEquals(1, 0);
//                }
//            });
//
//        act.getAsync(act.FILE_COLLECTION_NAME, "lgOW6jYP1hXPmHJKE4i2", new Callback() {
//                    @Override
//                    public void onSuccess(Map<String, Object> data, String message) {
//                        assertEquals(data.get("filename"), "/document/acc=1;doc=15");
//                        assertEquals(data.get("filepath"), "MxTtBm9zkTaesi86UH5uaqGKvlA2/_document_acc=1;doc=15");
//                        assertEquals(data.get("storageURL"), "https://firebasestorage.googleapis.com/v0/b/kleingroupv2.appspot.com/o/MxTtBm9zkTaesi86UH5uaqGKvlA2%2F_document_acc%3D1%3Bdoc%3D15?alt=media&token=112d10a8-42f1-4321-ba39-154b1d177cab");
//                        assertEquals(data.get("userID"), "En8fRBqxPiZ13HvOabUx7uOXN2T2");
//                    }
//
//                    @Override
//                    public void onFailure(String error, MyError.ErrorCode errorCode) {
//                        assertEquals(1, 0);
//                    }
//                });
//
//
//        act.getAsync(act.USER_COLLECTION_NAME, "badID", new Callback() {
//            @Override
//            public void onSuccess(Map<String, Object> data, String message) {
//                assertEquals(1,0);
//            }
//
//            @Override
//            public void onFailure(String error, MyError.ErrorCode errorCode) {
//                assertEquals(1,0);
//
//                assertEquals("No data", error);
//                assertEquals(MyError.ErrorCode.NOT_FOUND, errorCode);
//            }
//        });
//    }
}
