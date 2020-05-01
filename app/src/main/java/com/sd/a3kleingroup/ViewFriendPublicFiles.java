package com.sd.a3kleingroup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/*
    Goal here is that given a user's ID and knowing from the public file receive activity  know the following:
    1) this user is friends with the other user
    2) we should be able to retrieve the user friend's public files via the friends ID.

    Need to check:
    1) that the user has at least one file
 */

public class ViewFriendPublicFiles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_public_files);
    }
}
