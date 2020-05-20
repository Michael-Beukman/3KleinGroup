package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;
import com.sd.a3kleingroup.classes.UI.PublicFilesRecyclerHolder;
import com.sd.a3kleingroup.classes.Utils;
import com.sd.a3kleingroup.classes.callbacks.CallbackGeneric;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
    Goal here is that given a user's ID and knowing from the public file receive activity  know the following:
    1) this user is friends with the other user
    2) we should be able to retrieve the user friend's public files via the friends ID.

    Need to check:
    1) that the user has at least one file
 */

public class ViewFriendPublicFiles extends RecyclerViewFilesActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    MyError errorHandler;

    public boolean hasFiles = false; //assume false until proven otherwise.
//    public String friendID="MxTtBm9zkTaesi86UH5uaqGKvlA2";
    public String friendID= "eR1HwsAKAifArcER3CACMIzMxkF3";
    private dbUser friend;
    public String TAG = "View Friend Public Files";
    RecyclerAdapter myAdapter;
    public List<dbPublicFiles> friendsFiles;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view_friend_public_files);
        super.onCreate(savedInstanceState);
        errorHandler = new MyError(ViewFriendPublicFiles.this);
        LOG_TAG = "MY_"+TAG;
        getUserName();

    }

    @Override
    protected RecyclerViewClickListener getPopupListener() {
        return null;
    }

    @Override
    protected RecyclerViewClickListener getTextButtonListener() {
        return null;
    }

    @Override
    protected CallbackGeneric<QuerySnapshot> getMyCallback() {
        BaseActivity self = this;
        return new CallbackGeneric<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot param, String message) {
                Log.d(LOG_TAG, "SUCCESS getting data " + param.size());
                mRecyclerView = findViewById(R.id.avfpf_recycle);
                Log.d(LOG_TAG, "SUCCESS getting data THE THING IS " + mRecyclerView);

                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager viewManager = new LinearLayoutManager(self);
                myAdapter = new RecyclerAdapter(self, param, null, null);
                mRecyclerView.setAdapter(myAdapter);
                mRecyclerView.setLayoutManager(viewManager);
            }

            @Override
            public void onFailure(String message, MyError.ErrorCode errorCode) {

            }
        };
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


    /* UI Stuff */

    protected Query getInitialQuery(){
        return firebaseFirestore.collection("Public Files").whereEqualTo("user_id", friendID);
    }

    /**
     * This class is the recycler adapter class to show the public files.
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<PublicFilesRecyclerHolder> {

        RecyclerViewClickListener mTextBtnListener;
        RecyclerViewClickListener mPopUpListener;

        BaseActivity parentActivity;
        private ArrayList<dbPublicFiles> publicFiles;


        public RecyclerAdapter(BaseActivity parentActivity, QuerySnapshot data, RecyclerViewClickListener txtbtnlistener, RecyclerViewClickListener popuplistener) {
            mTextBtnListener = txtbtnlistener;
            mPopUpListener = popuplistener;
            this.parentActivity = parentActivity;
            publicFiles = new ArrayList<>();

            for (DocumentSnapshot d : data.getDocuments()) {
                dbPublicFiles toAdd = new dbPublicFiles(d);
                toAdd.setID(d.getId());
                // sets the Id, to make sure we have a reference to the agreement
                toAdd.setID(d.getId());
                publicFiles.add(toAdd);
            }
        }

        @NonNull
        @Override
        public PublicFilesRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parentActivity.getBaseContext());
            View view = layoutInflater.inflate(R.layout.view_recycler_public_view_friend_files, null, false);

            return new PublicFilesRecyclerHolder(view, new CallbackGeneric<dbPublicFiles>() {
                @Override
                public void onSuccess(dbPublicFiles param, String message) {
                    // we must update the who viewed public files.
                    if (!param.getID().equals("")){
                        Log.d(LOG_TAG, "This file has a good ID, we will update whoViewed Now. " +param.getHashmap());
                        updateWhoViewed(param.getID());

                    }else{
                        Log.d(LOG_TAG, "This file has a bad ID, we cannot update whoViewed " +param.getHashmap());
                    }
                    downloadFile(param);
                }

                @Override
                public void onFailure(String message, MyError.ErrorCode errorCode) {

                }
            });
        }



        //Provides a means to handle each view holder in the list
        @Override
        public void onBindViewHolder(@NonNull PublicFilesRecyclerHolder holder, int position) {

            dbPublicFiles publicFile = publicFiles.get(position);
            holder.setMyFile(publicFile);
            if (friend!=null)
                holder.setUserInfo(friend);
            Log.d(LOG_TAG, "Hey setting the holders params " + friend);
        }

        @Override
        public int getItemCount() {
            return publicFiles.size();
        }
    }

    /**
     * This gets the friend's details.
     */
    void getUserName(){
        Utils.getInstance().getUserFromID(friendID, new CallbackGeneric<dbUser>() {
            @Override
            public void onSuccess(dbUser param, String message) {
                friend = param;
                Log.d(LOG_TAG, "We got the friend data "+ message + " " + friend + " " + param);

                // now make all recyclerview holders use the user data.
                if (myAdapter!=null)
                    myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message, MyError.ErrorCode errorCode) {
                friend = new dbUser("Unknown", "Unknown");
                Log.d(LOG_TAG, "We got an error at getting friend data "+ message);
            }
        });
    }


    /**
     * This updates the whoviewedPublicFiles array thing and adds the current user's name.
     * @param id
     */
    private void updateWhoViewed(String id) {
        DocumentReference ref = FirebaseFirestore.getInstance().collection("WhoViewedPublicFiles")
                .document(id);
        String userName = "unknown";
        try {
         userName  =FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }catch (Exception e){

        }
        String finalUserName = userName;
        ref.get().addOnCompleteListener(t->{
                    if (t.isSuccessful()){
                        DocumentSnapshot r = t.getResult();
                        if (r!=null && r.exists()){
                            Log.d(LOG_TAG, "Document Who viewed exists, so we can update");
                            ref.update("Users", FieldValue.arrayRemove(finalUserName)).addOnCompleteListener(task2 -> {
                                // now we
                                Log.d(LOG_TAG, "Removing " +finalUserName +" was "+(task2.isSuccessful() ? "successful" : "unsuccessful") );
                                ref.update("Users", FieldValue.arrayUnion(finalUserName)).addOnCompleteListener(task3 -> {
                                    Log.d(LOG_TAG, "Adding " +finalUserName +" was "+(task3.isSuccessful() ? "successful" : "unsuccessful") );
                                });
                            });
                        }else{
                            Log.d(LOG_TAG, "Document Who viewed doesnt exist, so we have to set");
                            ArrayList<String> sss = new ArrayList<String>();
                            sss.add(finalUserName);
                            ref.set(new HashMap<String, Object>(){{
                                put("Users", sss);
                            }}).addOnCompleteListener(task3 -> {
                                Log.d(LOG_TAG, "Setting " + finalUserName +" was "+(task3.isSuccessful() ? "successful" : "unsuccessful") );
                            });
                        }
                    }else{
                        // too bad so sad
                        errorHandler.displayError("Could not update Who Viewed files.");

                    }
        });
    }

}
