package com.sd.a3kleingroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.UnfriendDialogFragment;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class FriendProfileActivity extends BaseActivity implements UnfriendDialogFragment.UnfriendDialogListener {

    private dbUser friend;
    private String myID;
    private TextView txtName, txtEmail, txtNumPublic;
    private RecyclerView recyclerView;
    private FriendProfileActivity.RecyclerAdapter mAdapter;
    private FloatingActionButton fab;
    private FirebaseFirestore db;
    private ArrayList<dbPublicFiles> publicFiles;
    private String TAG = "FriendProfile_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend_profile);
        super.onCreate(savedInstanceState);

        friend = new dbUser(getIntent().getStringExtra("docID"), getIntent().getStringExtra("email"), getIntent().getStringExtra("name"),getIntent().getStringExtra("notificationToken"));
        myID = getIntent().getStringExtra("myID");

        //Set up toolbar so we can navigate back to friend list
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        setElements();
        setFab();
        setupRV();
        getFileDataFromFirestore();
    }

    private void setElements() {
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        //We can set the text for these straight away
        txtEmail.setText(friend.getName());
        txtName.setText(friend.getEmail());

        //We need to query Firestore before this text can be set
        txtNumPublic = findViewById(R.id.txtNumPublicFiles);
    }

    //Set up the floating action button for unfriending.
    private void setFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnfriendDialogFragment dialog = new UnfriendDialogFragment();
                dialog.show(getSupportFragmentManager(), "unfriend dialog");
            }
        });
    }


    @Override
    public void onDialogPositiveClick() {
        unfriend();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        return;
    }

    private void unfriend() {
        //TODO: Unfriend by removing doc in Friends agreement and exit activity
        Log.d(TAG, "Unfriending");
        //This is pretty bad to have to do this query since we got the relevant Friend collection docID at some point in FriendListActivity
        //but the way the getAsync works makes it a mission to associate this id with the relevant friend object :p
        db.collection("Friends")
                .whereEqualTo("senderID", friend.getDocID()).whereEqualTo("recipientID", myID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "bye bye " + friend.getName() + " :)");
                                        //Go back to friend list and refresh friend list
                                        NavUtils.navigateUpFromSameTask(this);
                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "Error Unfriending", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        db.collection("Friends")
                .whereEqualTo("recipientID", friend.getDocID()).whereEqualTo("senderID", myID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, myID + " " + friend.getDocID());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "bye bye " + friend.getName() + " :)");
                                        //Go back to friend list and refresh friend list
                                        NavUtils.navigateUpFromSameTask(this);
                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "Error Unfriending", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setupRV() {
        recyclerView = findViewById(R.id.recycler_view);
        publicFiles = new ArrayList<>();
        mAdapter = new FriendProfileActivity.RecyclerAdapter(this, publicFiles, new FriendProfileActivity.AdapterListener() {
            //Callback for when specific public file is selected
            @Override
            public void onFileSelected(dbPublicFiles file) {
                Log.d(TAG, "selected " + file.getFileName());
                //TODO: add user to viewed of doc file.getID()?
                downloadFile(file);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
    }

    private void getFileDataFromFirestore() {
        //Callback for when data for a single public file is retrieved
        Callback cbPfile = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                dbPublicFiles f = new dbPublicFiles((String) data.get("encryption_key"), (String) data.get("file_name"), (String) data.get("file_path"), (String) data.get("file_path"), (String) data.get("file_storage"), (String) data.get("user_id"));
                f.setID(message);
                publicFiles.add(f);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                Log.e(TAG, errorCode.toString());
            }
        };

        CollectionReference collection = db.collection("Public Files");

        //Query for public files that belong to this friend
        Query query = collection.whereEqualTo("user_id", friend.getDocID());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    return;
                }

                txtNumPublic.setText(Integer.toString(value.size()));
                //Manually perform a join with the Users collection
                for (QueryDocumentSnapshot doc : value) {
                    cbPfile.onSuccess(doc.getData(), doc.getId());
                }
            }
        });
    }

    //---------------------------------------------------------------------------------
    //--------Classes and Interfaces for RecyclerView defined Beyond this point--------
    //---------------------------------------------------------------------------------

    public class RecyclerAdapter extends RecyclerView.Adapter<FriendProfileActivity.RecyclerAdapter.MyViewHolder> {

        private Context context;
        private ArrayList<dbPublicFiles> fileList;
        private FriendProfileActivity.AdapterListener listener;

        public RecyclerAdapter(Context context, ArrayList<dbPublicFiles> files, FriendProfileActivity.AdapterListener listener) {

            fileList = files;
            this.listener = listener;
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView txt_filename;

            public MyViewHolder(View view) {
                super(view);
                txt_filename = view.findViewById(R.id.txtbtn_filename);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // send selected contact in callback
                        listener.onFileSelected(fileList.get(getAdapterPosition()));
                    }
                });
            }
        }

        @Override
        public FriendProfileActivity.RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recycler_public_view_friend_files, parent, false);

            return new FriendProfileActivity.RecyclerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FriendProfileActivity.RecyclerAdapter.MyViewHolder holder, final int position) {
            final dbPublicFiles file = fileList.get(position);
            holder.txt_filename.setText(file.getFileName());
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }
    }

    public interface AdapterListener {
        void onFileSelected(dbPublicFiles file);
    }
}
