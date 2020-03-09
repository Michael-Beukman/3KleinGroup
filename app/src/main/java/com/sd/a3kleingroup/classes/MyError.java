package com.sd.a3kleingroup.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.sd.a3kleingroup.MainActivity;

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
        Log.e("MY_ERROR", "Failed with " + message);
    }

    public void displaySuccess(String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }
}
