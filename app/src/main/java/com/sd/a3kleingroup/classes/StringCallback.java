package com.sd.a3kleingroup.classes;

import java.util.HashMap;

public abstract class StringCallback{
    public abstract void onSuccess(String data, String message);

    public abstract void onFailure(String error, MyError.ErrorCode errorCode);
}
