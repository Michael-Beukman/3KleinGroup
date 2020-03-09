package com.sd.a3kleingroup;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.Utils;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

//@RunWith(AndroidJUnit4.class)
public class UtilsTest {
    @Rule
    public ActivityScenarioRule<MainActivity>  activityScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testGetEmailFromUser(){

        // first test for an email that is there.
        Callback tstCallBackSuccess = new Callback() {
            @Override
            public void onSuccess(HashMap<String, Object> data, String message) {
                Assert.assertEquals(data.get("userID"), "En8fRBqxPiZ13HvOabUx7uOXN2T2");
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                // bad
                Assert.assertEquals(1,0);
            }
        };
        Utils utils = Utils.getInstance();
        utils.getUserFromEmail("wurkez@gmail.com", tstCallBackSuccess);


        // now test email that is not in
        Callback tstCallBackFailure = new Callback() {
            @Override
            public void onSuccess(HashMap<String, Object> data, String message) {
                // this shouldnt happen
                Assert.assertEquals(1,0);
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                Assert.assertEquals(errorCode, MyError.ErrorCode.NOT_FOUND);
                Assert.assertEquals(error, "No Matching Email");
            }
        };

        utils.getUserFromEmail("badEmail@lol.com", tstCallBackFailure);
    }
}
