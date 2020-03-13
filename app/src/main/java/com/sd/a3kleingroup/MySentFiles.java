package com.sd.a3kleingroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.SingleSentFile;
import com.sd.a3kleingroup.classes.StringCallback;

import java.util.HashMap;

public class MySentFiles extends BaseActivity {
    private String LOG_TAG = "LOG_MySentFiles";
    private HashMap<String, Boolean> fileIsPending;
    private HashMap<String, Boolean> userIsPending;
    private HashMap<String, String> userIDs;
    private HashMap<String, String> fileIDs;

    private void getFileName(String fileID, StringCallback cb){
        if (fileIDs.containsKey(fileID)){
            cb.onSuccess(fileIDs.get(fileID), "");
        }else if (fileIsPending.containsKey(fileID)){
            // need to wait
        }else{
            // need to query

        }

    }

    private void getUserName(String userID){

    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView main;
        public ImageButton imgButton;
        public Holder(View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.msf_li_txtTitle);
            imgButton = itemView.findViewById(R.id.msf_li_imgBtn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sent_files);
        recyclerViewStuff();

    }

    void recyclerViewStuff(){
        Log.d(LOG_TAG, "Starting stuff");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = db.collection("Agreements").whereEqualTo("ownerID", user.getUid());

        FirestoreRecyclerOptions<SingleSentFile> response = new FirestoreRecyclerOptions.Builder<SingleSentFile>()
                .setQuery(query, SingleSentFile.class)
                .build();


        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<SingleSentFile, Holder>(response) {
            @Override
            public void onBindViewHolder(Holder holder, int position, SingleSentFile model) {
                Log.d(LOG_TAG, "Binding " + position + model.getUserID());
                holder.main.setText(model.getUserID());
                holder.imgButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(LOG_TAG, "Clicked " + position  + " " + model.getUserID());
                        showPopup(view);
                    }
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


}
