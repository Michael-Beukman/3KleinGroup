package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.UnfriendDialogFragment;
import com.sd.a3kleingroup.classes.db.dbUser;

public class FriendProfileActivity extends BaseActivity implements UnfriendDialogFragment.UnfriendDialogListener {

    private dbUser friend;
    private String myID;
    private TextView txtName, txtEmail, txtNumPublic;
    private FloatingActionButton fab;
    private FirebaseFirestore db;
    private String TAG = "FriendProfile_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend_profile);
        super.onCreate(savedInstanceState);

        friend = new dbUser(getIntent().getStringExtra("docID"), getIntent().getStringExtra("email"), getIntent().getStringExtra("name"),getIntent().getStringExtra("notificationToken"));
        myID = getIntent().getStringExtra("myID");

        //Set up toolbar so we can navigate back to friend list
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setElements();
        setFab();

        db = FirebaseFirestore.getInstance();
    }

    private void setElements() {
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        //We can set the text for these straight away
        txtEmail.setText(friend.getName());
        txtName.setText(friend.getEmail());

        //We need to query Firestore before this can be set
        txtNumPublic = findViewById(R.id.txtNumPublicFiles);
    }

    //Set up the floating action button for unfriending.
    private void setFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnfriendDialogFragment dialog = new UnfriendDialogFragment();
                dialog.show(getSupportFragmentManager(), "unfriend dialog");
            }
        });
    }


    @Override
    public void onDialogPositiveClick() {
        unfriend();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        return;
    }

    private void unfriend() {
        //TODO: Unfriend by removing doc in Friends agreement and exit activity
        Log.d(TAG, "Unfriending");
        //This is pretty bad to have to do this query since we got the relevant Friend collection docID at some point in FriendListActivity
        //but the way the getAsync works makes it a mission to associate this id with the relevant friend object :p
        db.collection("Friends")
                .whereEqualTo("senderID", friend.getDocID()).whereEqualTo("recipientID", myID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "bye bye " + friend.getName() + " :)");
                                        //Go back to friend list and refresh friend list
                                        NavUtils.navigateUpFromSameTask(this);
                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "Error Unfriending", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        db.collection("Friends")
                .whereEqualTo("recipientID", friend.getDocID()).whereEqualTo("senderID", myID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, myID + " " + friend.getDocID());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "bye bye " + friend.getName() + " :)");
                                        //Go back to friend list and refresh friend list
                                        NavUtils.navigateUpFromSameTask(this);
                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "Error Unfriending", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    //TODO: recycler view with public files
}
