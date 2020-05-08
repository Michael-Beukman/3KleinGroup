package com.sd.a3kleingroup.classes.callbacks;

import com.sd.a3kleingroup.classes.MyError;

public abstract class CallbackGeneric<T> {
    public abstract void onSuccess(T param, String message);

    public abstract void onFailure(String message, MyError.ErrorCode errorCode);
}
