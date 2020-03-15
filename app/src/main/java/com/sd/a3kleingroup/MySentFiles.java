package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.SingleSentFile;
import com.sd.a3kleingroup.classes.StringCallback;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MySentFiles extends BaseActivity {
    private final String USER_COLLECTION_NAME = "Users";
    private final String FILE_COLLECTION_NAME = "Files";
    private String LOG_TAG = "LOG_MySentFiles";
    // the adapter for the recyclerview
    FirestoreRecyclerAdapter adapter;
    /** [
     * 'Files' => ['fileID1' => {[file_data]}, 'fileID2' => {[file_data]}]
     * 'Users' => ['userID' => {[user_data]}]
     * ]
     */
    private HashMap<String, HashMap<String, Map<String, Object>>> details = new HashMap<String, HashMap<String, Map<String, Object>>>(){{
        put(USER_COLLECTION_NAME, new HashMap<String, Map<String, Object>>());
        put(FILE_COLLECTION_NAME, new HashMap<String, Map<String, Object>>());
    }};


    // This is a single item in our list
    class Holder extends RecyclerView.ViewHolder {
        TextView main;
        ImageButton imgButton;
        TextView userName;
        Callback cbUser;
        Callback cbFile;
        Holder(View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.msf_li_txtTitle);
            imgButton = itemView.findViewById(R.id.msf_li_imgBtn);
            userName = itemView.findViewById(R.id.msf_li_txtUserName);
            setCallbacks();
        }

        private void setCallbacks() {
            cbUser = new Callback() {
                @Override
                public void onSuccess(Map<String, Object> data, String message) {
                    userName.setText((String)data.get("name"));
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    userName.setText(error);

                }
            };

            cbFile = new Callback() {
                @Override
                public void onSuccess(Map<String, Object> data, String message) {
                    main.setText((String)data.get("filename"));
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    main.setText(error);

                }
            };
        }

        /**
         * Makes as if it is loading
         */
        public void setLoading() {
            main.setText("Loading...");
            userName.setText("Loading...");
        }
    }

    /**
     * Gets something async from the database, either File, or User with specified ID. Calls callback
     * @param collectionName
     * @param docID
     * @param cb
     */
    private void getAsync(String collectionName, String docID, Callback cb){
        if (details.get(collectionName).containsKey(docID)) cb.onSuccess((details.get(collectionName).get(docID)), "");
        else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(collectionName).document(docID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(LOG_TAG, "Got data " + documentSnapshot.getData() + " " + collectionName + " " + docID);
                    if (documentSnapshot.getData() == null){
                        cb.onFailure("No data", MyError.ErrorCode.NOT_FOUND);
                    }
                    else cb.onSuccess(documentSnapshot.getData(), "");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    cb.onFailure(e.getMessage(), MyError.ErrorCode.TASK_FAILED);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sent_files);
        recyclerViewStuff();
        doButtons();

    }

    /**
     * This puts the recyclerview up and makes everything work.
     */
    void recyclerViewStuff(){
        Log.d(LOG_TAG, "Starting stuff");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // All the agreements that are mine
        Query query = db.collection("Agreements").whereEqualTo("ownerID", user.getUid());

        FirestoreRecyclerOptions<SingleSentFile> response = new FirestoreRecyclerOptions.Builder<SingleSentFile>()
                .setQuery(query, SingleSentFile.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<SingleSentFile, Holder>(response) {
            @Override
            public void onBindViewHolder(Holder holder, int position, SingleSentFile model) {
                Log.d(LOG_TAG, "Binding " + position + model.getUserID());
                holder.main.setText(model.getUserID());
                // now we query for results

                // before we query, say loading per holder
                holder.setLoading();

                // get Username
                getAsync(USER_COLLECTION_NAME, model.getUserID(), holder.cbUser);
                // get filename
                getAsync(FILE_COLLECTION_NAME, model.getFileID(), holder.cbFile);

                holder.imgButton.setOnClickListener(view -> {
                    Log.d(LOG_TAG, "Clicked " + position  + " " + model.getUserID());
                    showPopup(view);
                });
            }
            @Override
            public Holder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.msf_list_item, group, false);
                return new Holder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        };

        RecyclerView mine = findViewById(R.id.msf_recAll);
        mine.setAdapter(adapter);
        adapter.startListening();
        RecyclerView.LayoutManager viewManager = new LinearLayoutManager(this);
        mine.setLayoutManager(viewManager);

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.new_game:
                        Log.d(LOG_TAG, "Clicked item 00");

                        return true;
                    case R.id.help:
                        Log.d(LOG_TAG, "Clicked item 11");

                        return true;
                    default:
                        return false;
            }
        }});
        popup.show();
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                Log.d(LOG_TAG, "Clicked item 0");

                return true;
            case R.id.help:
                Log.d(LOG_TAG, "Clicked item 1");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void doButtons(){
        Button btnApproved = findViewById(R.id.msf_btnApproved);
        btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                );
            }
        });
    }


}
