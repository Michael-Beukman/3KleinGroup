package com.sd.a3kleingroup;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.Filter;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.StringCallback;
import com.sd.a3kleingroup.classes.User;
import com.sd.a3kleingroup.classes.callbacks.NullCallback;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)

public class CallbackTest {
    @Rule
    public ActivityScenarioRule<MySentFiles> activityScenarioRule
            = new ActivityScenarioRule<>(MySentFiles.class);
    @Test
    public void testNullCallback(){
        Callback c = new NullCallback();
        c.onSuccess(null, null);
        c.onFailure(null, null);
        Assert.assertEquals(1,1);
    }

    @Test
    public void testFilter(){
        class F extends Filter<Integer>{
            @Override
            public void filter(ArrayList<Integer> arr) {

            }
        }
        F tmp = new F();
        tmp.filter(null);
        Assert.assertEquals(1,1);
    }
    public void testStringCallback(){
        class S extends StringCallback{
            @Override
            public void onSuccess(String data, String message) {

            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {

            }
        }
        S s = new S();
        s.onFailure(null, null);
        s.onSuccess(null, null);
        Assert.assertEquals(1,1);

    }
    @Test
    public void testError(){
        activityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MySentFiles>() {
            @Override
            public void perform(MySentFiles activity) {
                MyError tmp = new MyError(activity.getApplicationContext());
                tmp.displayError("Error");
                tmp.displaySuccess("Success");
                Assert.assertEquals(1,1);
            }
        });
    }

    @Test
    public void testUser(){
        User u = new User("", "", "", "id");
        Assert.assertEquals("id",u.getId());
    }
}
