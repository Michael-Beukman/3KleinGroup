package com.sd.a3kleingroup.classes;

import android.content.Context;
import android.widget.Toast;

public class MyError {
    public enum ErrorCode{
        NOT_FOUND,
        TASK_FAILED
    }
    Context context;

    public MyError(Context ctx){
        context = ctx;
    }


    /**
     * Displays an error in toast and also in a textview
     * @param message
     */
    public void displayError(String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
        // todo
    }

    public void displaySuccess(String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }
}
