package com.sd.a3kleingroup.classes.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
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
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.db.dbUser;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "1";
    private String TAG = "LOG_MyFirebaseMessagingService";
    private static int NOTIF_ID = 2;
    public void getToken(){
        Log.d(TAG, "Im getting a token");

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
        Log.e("current user", token);
        if (user == null){
            Log.d(TAG, "User Is null on new token, aborting");
            return;
        }
        dbUser myuser = new dbUser(user.getEmail(), user.getDisplayName(), token);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(user.getUid()).update(myuser.getHashmap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG,"Here at task " + task.isSuccessful() + " " + task.getException() + " " + user.getUid());
            }
        });

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "RECEIVED MESSAGE");
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "RECEIVED MESSAGE " + remoteMessage.toString());

        createNotificationChannel();
        String title = "New message";
        String body = "You received a new messsage";
        try {
            title = remoteMessage.getNotification().getTitle();
        }catch (NullPointerException ignored){}

        try {
            body = remoteMessage.getNotification().getBody();
        }catch (Exception ignored){}

        displayNotification(title, body);
        return ;
        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1590, builder.build());
         */
    }

    @Override
    public void onDeletedMessages(){
        Log.d(TAG, "On delete");
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("Channel Name");
            String description = ("Channel Desc");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    //Notification builder
    private void displayNotification(String title, String body){
        /* ic notification is guys dino
        Priority refers to the notification's priority itself, currently left on default in case we want to have varying priorities */
        NotificationCompat.Builder noteBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_guydino)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //This completes the basic notification builder

        //Now to manage this notification system
        NotificationManagerCompat noteM = NotificationManagerCompat.from(this);
        /*requires the notification ID and this ID is used to update and delete the notifications but currently left this as one assuming we just want a basic notification
        Only have one notification at current(a basic one, we could have other ones should we want)*/
        noteM.notify(NOTIF_ID++, noteBuilder.build());
    }
}
