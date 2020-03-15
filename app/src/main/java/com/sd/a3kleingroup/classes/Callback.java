package com.sd.a3kleingroup.classes;

import java.util.HashMap;
import java.util.Map;

public abstract class Callback {
    public abstract void onSuccess(Map<String, Object> data, String message);

    public abstract void onFailure(String error, MyError.ErrorCode errorCode);
}
