package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.util.TypedValue;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.Filter;
import com.sd.a3kleingroup.classes.Holder;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.SingleSentFile;
import com.sd.a3kleingroup.classes.StringCallback;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MySentFiles extends BaseActivity {
    final String USER_COLLECTION_NAME = "Users";
    final String FILE_COLLECTION_NAME = "Files";

    private String LOG_TAG = "LOG_MySentFiles";
    // The file we have currently selected, and are looking at the options for
    private SingleSentFile currentFileSelected = null;
    private MyError errorHandler;

    // Our filtering buttons
    Button btnApproved;
    Button btnAll;
    Button btnPending;

    // the adapter for the recyclerview
    MyRecyclerViewAdapter adapter;
    /**
     * This caches the data we grab from the DB, so it's faster.
     * [
     * 'Files' => ['fileID1' => {[file_data]}, 'fileID2' => {[file_data]}]
     * 'Users' => ['userID' => {[user_data]}]
     * ]
     */
    private HashMap<String, HashMap<String, Map<String, Object>>> details = new HashMap<String, HashMap<String, Map<String, Object>>>(){{
        put(USER_COLLECTION_NAME, new HashMap<String, Map<String, Object>>());
        put(FILE_COLLECTION_NAME, new HashMap<String, Map<String, Object>>());
    }};

    /**
     * Adapter for recyclerview. A Custom adapter is needed, since we have to do filtering
     */
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<Holder> {
        private static final String LOG_TAG = "LOG_MyRecyclerViewAdap";
        private ArrayList<SingleSentFile> mDataset; // main, i.e. base copy
        private ArrayList<SingleSentFile> filtered; // the copy of the data with the filter applied
        private Filter<SingleSentFile> currentFilter = null;

        /**
         * Creates with a query snapshot.
         * @param data
         */
        public MyRecyclerViewAdapter(QuerySnapshot data) {
            // make data
            mDataset = new ArrayList<>();
            for (DocumentSnapshot d: data.getDocuments()){
                mDataset.add(
                        new SingleSentFile((String)d.get("fileID"), (String)d.get("userID"), ((Timestamp)d.get("validUntil")).toDate(), (String)d.get("ownerID"), d.getId())
                );
            }
            filtered = (ArrayList<SingleSentFile>) mDataset.clone();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public Holder onCreateViewHolder(ViewGroup group, int i) {
            View view = LayoutInflater.from(group.getContext())
                    .inflate(R.layout.msf_list_item, group, false);
            return new Holder(view);
        }

        /**
         * Bind The view and the element.
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(Holder holder, int position) {
            SingleSentFile model = filtered.get(position);

            Log.d(LOG_TAG, "Binding " + position + model.getUserID());
            holder.main.setText(model.getUserID());

            // before we query, make text 'loading...' on holder
            holder.setLoading();

            // get Username
            getAsync(USER_COLLECTION_NAME, model.getUserID(), holder.cbUser);
            // get filename
            getAsync(FILE_COLLECTION_NAME, model.getFileID(), holder.cbFile);

            // When clicking the hamburger,we need to show a popup and set the current selected file.
            holder.imgButton.setOnClickListener(view -> {
                Log.d(LOG_TAG, "Clicked " + position  + " " + model.getUserID());

                // set the current item we are viewing, and showing options for.
                currentFileSelected = model;

                // show popup
                showPopup(view);
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return filtered.size();
        }

        /**
         * Filters The dataset.
         * @param filter
         */
        public void filter(Filter<SingleSentFile> filter){
            currentFilter = filter;
            filtered = (ArrayList<SingleSentFile>) mDataset.clone();
            filter.filter(filtered);
            notifyDataSetChanged();
        }

        /**
         * Filters the dataset again with the last set filter.
         */
        public void filterWithCurrentFilter(){
            if (currentFilter == null){
                return;
            }
            this.filter(currentFilter);
        }
    }

    /**
     * Gets something async from the database, either File, or User with specified ID. Calls callback
     * @param collectionName
     * @param docID
     * @param cb
     */
    protected void getAsync(String collectionName, String docID, Callback cb){
        System.out.println("REEE " + details.get(collectionName).containsKey(docID));

        if (details.get(collectionName).containsKey(docID)) cb.onSuccess((details.get(collectionName).get(docID)), "");
        else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            System.out.println("REEE " + db);

            db.collection(collectionName).document(docID).get().addOnSuccessListener(documentSnapshot -> {
                Log.d(LOG_TAG, "Got data " + documentSnapshot.getData() + " " + collectionName + " " + docID);
                if (documentSnapshot.getData() == null){
                    cb.onFailure("No data", MyError.ErrorCode.NOT_FOUND);
                    System.out.println("NEEE");

                }
                else {
                    cb.onSuccess(documentSnapshot.getData(), "");
                    System.out.println("NEEE");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    cb.onFailure(e.getMessage(), MyError.ErrorCode.TASK_FAILED);
                    System.out.println("NEEE");
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sent_files);
        errorHandler = new MyError(this.getApplicationContext());
        MyFirebaseMessagingService x = new MyFirebaseMessagingService();
        Log.d(LOG_TAG, String.valueOf(x));
        x.getToken();
        doButtons();
        recyclerViewStuff();
    }

    /**
     * This puts the recyclerview up and makes everything work.
     */
    void recyclerViewStuff(){
        Log.d(LOG_TAG, "Starting stuff");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.d(LOG_TAG, "Error, user is NULL at recyclerviewstuff");
            return;
        }

        // All the agreements that are mine
        Query query = db.collection("Agreements").whereEqualTo("ownerID", user.getUid());

        RecyclerView mine = findViewById(R.id.msf_recAll);

        RecyclerView.LayoutManager viewManager = new LinearLayoutManager(this);

        // Add a snapshot listener, so that it updates live
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e == null){
                // good
                adapter = new MyRecyclerViewAdapter(queryDocumentSnapshots);
                mine.setAdapter(adapter);

                mine.setLayoutManager(viewManager);
            }else{
                // todo error
                errorHandler.displayError("An error occurred when trying to load data (" + e.getMessage() + ")");
                Log.d(LOG_TAG, "Error at query snapshotlistener "+ e.getMessage());
            }
        });
    }

    /**
     * Creates popup menu
     * @param v
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            String stuff =  currentFileSelected.getDocID() + " "+ currentFileSelected.getOwnerID() + " " + currentFileSelected.getValidUntil().toString();
            switch (item.getItemId()) {
                case R.id.Revoke:
                    Log.d(LOG_TAG, "Revoking " + stuff);
                    revoke(currentFileSelected);
                    return true;
                case R.id.ApproveTomo:
                    Log.d(LOG_TAG, "Approving Til tomo " + stuff);
                    approveTilTomorrow(currentFileSelected);
                    return true;
                case R.id.ApproveAlways:
                    Log.d(LOG_TAG, "Approving Always " + stuff);
                    approveIndefinitely(currentFileSelected);
                    return true;
                default:
                    return false;
        }
    });
        popup.show();
    }

    /**
     * Colours all buttons default, except for clicked one
     * @param clicked
     */
    void colourButtons(Button clicked){
        Resources.Theme theme = getApplicationContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
        for (Button btn: new Button[]{btnAll, btnApproved, btnPending}) {
                btn.getBackground().clearColorFilter();
        }
        clicked.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Sets all button onClicks and such.
     */
    void doButtons(){
        abstract class Base extends Filter<SingleSentFile>{
            public abstract boolean shouldBeIn(SingleSentFile f);
            @Override
            public void filter(ArrayList<SingleSentFile> arr) {
                int N = arr.size();
                for (int i= N -1 ; i>=0; i--){
                    SingleSentFile f = arr.get(i);
                    if (!shouldBeIn(f)){
                        arr.remove(i);
                    }
                }
            }
        }
        btnApproved = findViewById(R.id.msf_btnApproved);
        btnAll = findViewById(R.id.msf_btnAll);
        btnPending = findViewById(R.id.msf_btnPending);

        btnApproved.setOnClickListener(view -> {
            colourButtons(btnApproved);
            adapter.filter(new Base() {
                @Override
                public boolean shouldBeIn(SingleSentFile f) {
                    return f.getValidUntil().after(new Date());
                }
            });
        });

        // all of them
        btnAll.setOnClickListener(view -> {
            colourButtons(btnAll);
            adapter.filter(new Base() {
                @Override
                public boolean shouldBeIn(SingleSentFile f) {
                    return true;
                }
            });
        });

        // only pending, i.e. not approved
        btnPending.setOnClickListener(view -> {
            colourButtons(btnPending);
            adapter.filter(new Base() {
                @Override
                public boolean shouldBeIn(SingleSentFile f) {
                    return f.getValidUntil().before(new Date());

                }
            });
        });
    }

    /**
     * Arbitrary function to change the date of a file in firebase. Handles UI updates and firebase stuff.
     */
    void changePermissions(Date d, SingleSentFile file){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date tmp = file.getValidUntil();

        // set date
        file.setValidUntil(d);

        // filter again, so that this gets hidden if only approved
        try {
            adapter.filterWithCurrentFilter();
        }catch (Exception e){
            Log.d(LOG_TAG, "Error with filter current filter");
        }
        // do query
        db.collection("Agreements").document(file.getDocID()).update(
                "validUntil", d
        ).addOnFailureListener(e -> {
            // it failed, so roll back changes
            // set date back
            file.setValidUntil(tmp);

            // filter again
            adapter.filterWithCurrentFilter();

            // show error
            errorHandler.displayError("Could not update Database (" + e.getMessage()  + ")");
        }).addOnSuccessListener(aVoid -> {
            // success
            errorHandler.displaySuccess("Successfully changed permissions");
        });


    }

    /**
     * Revokes the permissions for this agreement by setting validUntil in the past.
     * @param file
     */
    void revoke(SingleSentFile file){
        changePermissions(dbAgreement.getDateInPast(), file);
    }

    /**
     * Approves until tomorrow
     * @param file
     */
    void approveTilTomorrow(SingleSentFile file){
        changePermissions(dbAgreement.getDateTomorrow(), file);
    }

    /**
     * Approves indefinitely
     * @param file
     */
    void approveIndefinitely(SingleSentFile file){
        changePermissions(dbAgreement.getDate20YearsInfuture(), file);
    }


}