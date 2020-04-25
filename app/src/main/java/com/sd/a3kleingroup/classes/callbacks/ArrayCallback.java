package com.sd.a3kleingroup.classes.callbacks;

import com.sd.a3kleingroup.classes.MyError;

import java.util.ArrayList;
import java.util.Map;

public abstract class ArrayCallback<T> {
    public abstract void onSuccess(ArrayList<T> arr, String message);

    public abstract void onFailure(String error, MyError.ErrorCode errorCode);
}
