package com.sd.a3kleingroup.classes.Messaging;

import android.app.Notification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sd.a3kleingroup.MainActivity;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.db.dbUser;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "LOG_MyFirebaseMessagingService";
    public void getToken(){
        Log.w(TAG, "Im getting a token");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        onNewToken(token);
                    }
                });
    }
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // update token
        // sendRegistrationToServer(token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbUser myuser = new dbUser(user.getEmail(), user.getDisplayName(), token);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(user.getUid()).update(myuser.getHashmap());

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "RECEIVED MESSAGE");
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "RECEIVED MESSAGE");
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(123, notification);
    }

    @Override
    public void onDeletedMessages(){
        Log.d(TAG, "On delete");
    }

}
