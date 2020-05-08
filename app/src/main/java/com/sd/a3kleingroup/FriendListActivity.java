package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.AddFriendDialogFragment;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class FriendListActivity extends BaseActivity implements AddFriendDialogFragment.AddFriendDialogListener {

    private static final String TAG = FriendListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private ArrayList<dbUser> friendsList;
    private SearchView searchView;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend_list);
        super.onCreate(savedInstanceState);

//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(1));
        //Toolbar stuff
        Toolbar toolbar = findViewById(R.id.toolbar);
        //sets the toolbar as the app bar for the activity
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        setupRV();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getFriendDataFromFireStore();
    }

    private void getFriendDataFromFireStore() {

        //Callback for when data for a single friend is retrieved
        Callback cbUser = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                dbUser u = new dbUser((String)data.get("email"),(String)data.get("name"),(String)data.get("notificationToken"));
                friendsList.add(u);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                Log.e(TAG, errorCode.toString());
            }
        };

        CollectionReference collection = db.collection("Friends");

        //Query for friends that we have accepted (i.e where we were recipient of friend request)
        Query query = collection.whereEqualTo("recipientID", user.getUid()).whereEqualTo("accepted", true);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    return;
                }

                //Manually perform a join with the Users collection
                for (QueryDocumentSnapshot doc : value) {
                    getAsync("Users", (String) doc.get("senderID"), cbUser);
                }
                //Log.d(TAG, "Current cites in CA: " + cities);
            }
        });

        //Query for friends that have accepted us (i.e where we were sender of friend request )
        query = collection.whereEqualTo("senderID", user.getUid()).whereEqualTo("accepted", true);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    return;
                }

                //Manually perform a join with the Users collection
                for (QueryDocumentSnapshot doc : value) {
                    getAsync("Users", (String) doc.get("recipientID"), cbUser);
                }
                //Log.d(TAG, "Current cites in CA: " + cities);
            }
        });
    }


    protected void getAsync(String collectionName, String docID, Callback cb){

        System.out.println("REEE " + db);
        db.collection(collectionName).document(docID).get().addOnSuccessListener(documentSnapshot -> {
            Log.d(TAG, "Got data " + documentSnapshot.getData() + " " + collectionName + " " + docID);
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


    private void setupRV() {
        recyclerView = findViewById(R.id.recycler_view);
        whiteNotificationBar(recyclerView);
        friendsList = new ArrayList<>();
        mAdapter = new RecyclerAdapter(this, friendsList, new FriendAdapterListener() {
            //Callback for when specific friend is selected
            @Override
            public void onFriendSelected(dbUser friend) {
                Log.d(TAG, "selected " + friend.getName());
                gotoFriend();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
    }

    private void gotoFriend() {
        //TODO: open friend's profile activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_toolbar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    //callback method for when user selects one of the app bar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }else if(id == R.id.action_add){
            AddFriendDialogFragment dialog = new AddFriendDialogFragment();
            dialog.show(getSupportFragmentManager(), "add friend dialog");
        }

        return super.onOptionsItemSelected(item);
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(String input_email) {
        // User touched the dialog's positive button (i.e. user wants to send a friend request to user with the email entered in the dialog)
        //First validate inputted email

        //write to Friends collection in Firestore which triggers a notification to be sent
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        return;
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    //---------------------------------------------------------------------------------
    //--------Classes and Interfaces for RecyclerView defined Beyond this point--------
    //---------------------------------------------------------------------------------

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements Filterable {

        private Context context;
        private ArrayList<dbUser> friendList;
        private ArrayList<dbUser> friendListFiltered;
        private FriendAdapterListener listener;

        public RecyclerAdapter(Context context, ArrayList<dbUser> friends, FriendAdapterListener listener) {

            friendList = friends;
            this.listener = listener;
            this.context = context;
            friendListFiltered = friendList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView txtName, txtEmail;
            public dbUser userInfo;
            public Callback cbUser;

            public MyViewHolder(View view) {
                super(view);
                txtName = view.findViewById(R.id.name);
                txtEmail = view.findViewById(R.id.email);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // send selected contact in callback
                        listener.onFriendSelected(friendListFiltered.get(getAdapterPosition()));
                    }
                });
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_recycler_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final dbUser friend = friendListFiltered.get(position);
            holder.txtName.setText(friend.getName());
            holder.txtEmail.setText(friend.getEmail());
        }

        @Override
        public int getItemCount() {
            return friendListFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        friendListFiltered = friendList;
                    } else {
                        ArrayList<dbUser> filteredList = new ArrayList<>();
                        for (dbUser row : friendList) {

                            // here we are looking for name or email match
                            if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getEmail().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        }

                        friendListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = friendListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    friendListFiltered = (ArrayList<dbUser>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public interface FriendAdapterListener {
        void onFriendSelected(dbUser friend);
    }
}
