package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import org.w3c.dom.Document;

import java.util.List;

/*
    Goal here is that given a user's ID and knowing from the public file receive activity  know the following:
    1) this user is friends with the other user
    2) we should be able to retrieve the user friend's public files via the friends ID.

    Need to check:
    1) that the user has at least one file
 */

public class ViewFriendPublicFiles extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public boolean hasFiles = false; //assume false until proven otherwise.
    public String friendID;
    public String TAG = "View Friend Public Files";
    public List<dbPublicFiles> friendsFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_public_files);
    }


    /*
    Checking to see if this user's friend has a file or not
     */
    public void friendHasFiles(){
        firebaseFirestore.collection("Public Files").whereEqualTo("user_id", friendID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.d(TAG, "Friend has no documents.");
                    //Notify the user in some visual fashion - maybe a text view
                }
                else{
                    Log.d(TAG, "Friend has some documents");
                    hasFiles = true;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure to see if friend has files");
                Toast.makeText(ViewFriendPublicFiles.this, "Failed to see if friend has files", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    retrieve the friend's files
     */

    public void getFriendsFiles(){
        firebaseFirestore.collection("Public Files").whereEqualTo("user_id", friendID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = (List<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                    friendsFiles.add(documentSnapshot.toObject(dbPublicFiles.class));
                    Log.d(TAG, "loading doc " + documentSnapshot.getId());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure to get the files");
            }
        });
    }

}
