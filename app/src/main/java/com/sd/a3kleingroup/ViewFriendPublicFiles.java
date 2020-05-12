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
import com.google.firebase.firestore.DocumentSnapshot;
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


    public boolean hasFiles = false; //assume false until proven otherwise.
    public String friendID="MxTtBm9zkTaesi86UH5uaqGKvlA2";
    private dbUser friend;
    public String TAG = "View Friend Public Files";
    RecyclerAdapter myAdapter;
    public List<dbPublicFiles> friendsFiles;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view_friend_public_files);
        super.onCreate(savedInstanceState);
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
     * Expropriated straight from Guy's code.
     * @param myFile
     */
    public void downloadFile(dbPublicFiles myFile){
        Context ctx = getBaseContext();
        Log.d("MY_HOLDER", "Downloading File");
        Toast.makeText(ctx, "Downloading File", Toast.LENGTH_SHORT).show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(myFile.getFilePath());

        final long ONE_MEGABYTE = 1024 * 1024 * 10;
        fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            //bytes is of type byte[]
            InputStream stream = new ByteArrayInputStream(bytes);
            try {
                //Create temp file to store file so that external apps can be used to open it.
                File tmpFile = File.createTempFile("tmp", ".pdf");
                OutputStream outputStream = new FileOutputStream(tmpFile);
                IOUtils.copyStream(stream, outputStream);
                Uri path = FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".provider", tmpFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                tmpFile.deleteOnExit();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });


    }
}
