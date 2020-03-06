package com.sd.a3kleingroup.classes;

import java.util.HashMap;

public abstract class Callback {
    public abstract void onSuccess(HashMap<String, Object> data, String message);

    public abstract void onFailure(String error, MyError.ErrorCode errorCode);
}
