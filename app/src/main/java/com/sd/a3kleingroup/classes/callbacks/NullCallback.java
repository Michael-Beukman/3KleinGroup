package com.sd.a3kleingroup.classes.callbacks;

import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;

import java.util.Map;

/**
 * A callback that does nothing
 */
public class NullCallback  extends Callback {
    @Override
    public void onSuccess(Map<String, Object> data, String message) {

    }

    @Override
    public void onFailure(String error, MyError.ErrorCode errorCode) {

    }
}
