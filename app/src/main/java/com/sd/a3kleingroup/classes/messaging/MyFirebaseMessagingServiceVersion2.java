package com.sd.a3kleingroup.classes.messaging;

import android.app.Service;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingServiceVersion2 extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("LOG_MyMServiceVersion2", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }
}
