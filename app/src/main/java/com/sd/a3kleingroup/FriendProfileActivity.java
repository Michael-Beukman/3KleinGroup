package com.sd.a3kleingroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.db.dbUser;

public class FriendProfileActivity extends BaseActivity {

    private dbUser friend;
    private String myID;
    private TextView txtName, txtEmail, txtNumPublic;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend_profile);
        super.onCreate(savedInstanceState);

        friend = new dbUser(getIntent().getStringExtra("id"), getIntent().getStringExtra("email"), getIntent().getStringExtra("name"),getIntent().getStringExtra("notificationToken"));
        myID = getIntent().getStringExtra("myID");

        //Set up toolbar so we can navigate back to friend list
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setElements();
        setFab();
    }

    private void setFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Unfriend by removing doc in Friends agreement and exit activity
            }
        });
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

    //TODO: recycler view with public files
}
