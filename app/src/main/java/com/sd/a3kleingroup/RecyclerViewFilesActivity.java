package com.sd.a3kleingroup;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.internal.firebase_auth.zzew;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.RecyclerHolder;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;
import com.sd.a3kleingroup.classes.UI.PublicFilesRecyclerHolder;
import com.sd.a3kleingroup.classes.UI.ReceiveFilesSorter;
import com.sd.a3kleingroup.classes.callbacks.CallbackGeneric;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A general class for when there is a recyclerview showing some files.
 * T is the recyclerview adapter.
 */

public abstract class RecyclerViewFilesActivity extends BaseActivity {
    FirebaseFirestore db;
    RecyclerView mRecyclerView;

    FirebaseFunctions mFirebaseFunctions;

    protected CallbackGeneric<QuerySnapshot> myCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCallback = getMyCallback();
        setUpFireStore();
        mFirebaseFunctions = FirebaseFunctions.getInstance();

        setUpRecyclerView();
    }

    /**
     * This sets up the recyclerview, using a query snapshot listener.
     */
    protected void setUpRecyclerView() {
        if (myCallback == null) myCallback = getMyCallback();
        Query query = getInitialQuery();
        mRecyclerView = findViewById(R.id.avfpf_recycle);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager viewManager = new LinearLayoutManager(this);

        RecyclerViewClickListener textButtonListener = getTextButtonListener();

        RecyclerViewClickListener popUpListener = getPopupListener();

        // Add a snapshot listener, so that it updates live
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e == null) {
                myCallback.onSuccess(queryDocumentSnapshots, "Success");
            } else {
                // todo error
                Log.d(LOG_TAG, "Error at query snapshotlistener " + e.getMessage());
                myCallback.onFailure("Failed to get data from query " + e.getMessage(), MyError.ErrorCode.TASK_FAILED);
            }
        });
    }

    /**
     * Sets up firestore Database.
     */
    private void setUpFireStore() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * This should return the listener for when a popup gets clicked on the recyclerview
     * @return
     */
    protected abstract RecyclerViewClickListener getPopupListener();

    /**
     * This should return the listener for when a recyclerview item is clicked on the recyclerview
     * @return
     */
    protected abstract RecyclerViewClickListener getTextButtonListener();

    /**
     * This should return the initial Query for the recyclerview, the one the callback will get called with.
     * @return
     */
    protected abstract Query getInitialQuery();

    /**
     * The callback that gets called after getting the query, and on every subsequent query update.
     * @return
     */
    protected abstract CallbackGeneric<QuerySnapshot> getMyCallback();

}
