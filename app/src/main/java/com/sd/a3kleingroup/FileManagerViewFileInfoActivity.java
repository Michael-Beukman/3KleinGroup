package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

public class FileManagerViewFileInfoActivity extends AppCompatActivity {

    dbPublicFiles file;
    String filename;
    String filepath;
    Button deletefile;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager_file_info);

    }

    public void setElements(){
        deletefile = findViewById(R.id.public_file_delete_file);
        recyclerView = findViewById(R.id.file_manager_info_recycler);
    }

    public void getData(){//check and see if the data exists
        if(getIntent().hasExtra("File name") && getIntent().hasExtra("File path")){//if this data exists
            filename = getIntent().getStringExtra("File name");
            filepath = getIntent().getStringExtra("File path");
        }
        else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteFileStorage(){ // should work
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference fileToDelete = storageReference.child(userID).child(filename);
        fileToDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(FileManagerViewFileInfoActivity.this, "File is deleted from storage", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FileManagerViewFileInfoActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void deleteFileFirestore(){


    }
}
