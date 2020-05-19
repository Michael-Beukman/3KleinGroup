package com.sd.a3kleingroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.FriendRequest;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class FriendRequestsActivity extends BaseActivity {

    private String TAG = "FriendRequests_TAG";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private FriendRequestsActivity.RecyclerAdapter mAdapter;
    ArrayList<String> friendRequestIDs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend_requests);
        super.onCreate(savedInstanceState);

        //Get List of request IDs (PKs for Friends collection)
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        friendRequestIDs = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        Log.d(TAG, friendRequestIDs.toString());

        //Set up toolbar so we can navigate back to friend list
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        
        setUpRV();
    }

    private void declineRequest(FriendRequest request) {
        //We delete the request from firestore so that it does not show up anymore
        DocumentReference requestRef = db.collection("Friends").document(request.getRequestID());
        requestRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    removeRequestFromRV(request.getRequestID());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));

    }

    private void acceptRequest(FriendRequest request) {
        //We Make them friends by updating field accepted to true on Firestore
        DocumentReference requestRef = db.collection("Friends").document(request.getRequestID());
        requestRef.update("accepted", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    removeRequestFromRV(request.getRequestID());

                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating document", e));

    }

    private void removeRequestFromRV(String requestID) {
        //TODO: dont think this is to great
        friendRequestIDs.remove(requestID);
        mAdapter.notifyDataSetChanged();
    }

    private void setUpRV() {
        recyclerView = findViewById(R.id.recycler_view);
        blackNotificationBar(recyclerView);
        FriendRequestsActivity.AdapterListener onDecline = request -> declineRequest(request);
        FriendRequestsActivity.AdapterListener onAccept = request -> acceptRequest(request);
        mAdapter = new FriendRequestsActivity.RecyclerAdapter(this, friendRequestIDs, onDecline, onAccept);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
    }


    private void blackNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    //---------------------------------------------------------------------------------
    //--------Classes and Interfaces for RecyclerView defined Beyond this point--------
    //---------------------------------------------------------------------------------

    public class RecyclerAdapter extends RecyclerView.Adapter<FriendRequestsActivity.RecyclerAdapter.MyViewHolder> {

        private Context context;
        private ArrayList<String> requestIDList;
        private FriendRequestsActivity.AdapterListener decline_listener;
        private FriendRequestsActivity.AdapterListener accept_listener;

        public RecyclerAdapter(Context context, ArrayList<String> requestIDs, FriendRequestsActivity.AdapterListener decline_listener, FriendRequestsActivity.AdapterListener accept_listener) {

            requestIDList = requestIDs;
            this.decline_listener = decline_listener;
            this.accept_listener = accept_listener;
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView txt_name, txt_email;
            public Button btnDecline, btnAccept;
            public FriendRequest friendRequest;
            public Callback cbRequester;

            public MyViewHolder(View view) {
                super(view);
                friendRequest = new FriendRequest();
                txt_name = view.findViewById(R.id.txt_name);
                txt_email = view.findViewById(R.id.txt_email);
                btnDecline = view.findViewById(R.id.btnDecline);
                btnAccept = view.findViewById(R.id.btnAccept);

                btnDecline.setOnClickListener(
                        v -> decline_listener.onRequestSelected(friendRequest));

                btnAccept.setOnClickListener(
                        v -> accept_listener.onRequestSelected(friendRequest));

                cbRequester = new Callback() {
                    @Override
                    public void onSuccess(Map<String, Object> data, String message) {
                        dbUser requester = new dbUser(message,(String)data.get("email"),(String)data.get("name"),(String)data.get("notificationToken"));
                        friendRequest.setPotentialFriend(requester);
                        txt_name.setText(requester.getName());
                        txt_email.setText(requester.getEmail());
                    }

                    @Override
                    public void onFailure(String error, MyError.ErrorCode errorCode) {
                        txt_name.setText("error");
                        txt_email.setText("error");
                    }
                };
            }

            public void setLoading() {
                txt_name.setText("Loading...");
                txt_email.setText("Loading...");
            }
        }

        @Override
        public FriendRequestsActivity.RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_friend_request, parent, false);

            return new FriendRequestsActivity.RecyclerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FriendRequestsActivity.RecyclerAdapter.MyViewHolder holder, final int position) {
            final String requestID = requestIDList.get(position);
            holder.friendRequest.setRequestID(requestID);

            holder.setLoading();

            //TODO: get userID :(
            db.collection("Friends").document(requestID).get().addOnSuccessListener(documentSnapshot -> {
                getAsync("Users", (String) documentSnapshot.getData().get("senderID"), holder.cbRequester);
            });
        }

        @Override
        public int getItemCount() {
            return requestIDList.size();
        }
    }

    public interface AdapterListener {
        void onRequestSelected(FriendRequest request);
    }
}
