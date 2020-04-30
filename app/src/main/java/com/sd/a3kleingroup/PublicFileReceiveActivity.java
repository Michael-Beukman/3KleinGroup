package com.sd.a3kleingroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.classes.UI.PublicFileReceiveAdapter;

import java.util.ArrayList;


//This class serves as a recycler adapter
public class PublicFileReceiveActivity extends AppCompatActivity {

    Button fetchFile; //button to fetch the files
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    // TODO: 2020/04/30 make a way to delete information, probably the best way to do this would be to have a public file manage activity 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_public_file_recieve);
        setContentView(R.layout.public_recycler_view); // will require to be changed as super basic
        RecyclerView publicRecyclerView = findViewById(R.id.myBadView);
        publicRecyclerView.setLayoutManager(new LinearLayoutManager(PublicFileReceiveActivity.this));
        //adapter for this view to populate the recycler view with items
        //pass it the pieces it requires
        PublicFileReceiveAdapter publicFileReceiveAdapter = new PublicFileReceiveAdapter(publicRecyclerView, PublicFileReceiveActivity.this, new ArrayList<String>(), new ArrayList<>());
        //attach this adapter
        publicRecyclerView.setAdapter(publicFileReceiveAdapter);
    }


    
    

}
