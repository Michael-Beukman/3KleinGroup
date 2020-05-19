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
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import com.sd.a3kleingroup.classes.Utils;
import com.sd.a3kleingroup.classes.db.dbFriends;
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
    int countFriends=-1;
    boolean havePerformedQueries = false;

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
                dbUser u = new dbUser(message,(String)data.get("email"),(String)data.get("name"),(String)data.get("notificationToken"));
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

                countFriends+=value.size()+1;
                //Manually perform a join with the Users collection
                for (QueryDocumentSnapshot doc : value) {
                    getAsync("Users", (String) doc.get("senderID"), cbUser);
                }
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

                //Checked when pressing action bar add friend. Used to make sure all friends have been fetched
                havePerformedQueries=true;
                countFriends+=value.size();

                //Manually perform a join with the Users collection
                for (QueryDocumentSnapshot doc : value) {
                    getAsync("Users", (String) doc.get("recipientID"), cbUser);
                }
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
                gotoFriend(friend);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
    }

    private void gotoFriend(dbUser friend) {
        Intent intent = new Intent(getBaseContext(), FriendProfileActivity.class);

        //Pass stuff to the profile activity so it doesn't have to look for it in Firebase
        intent.putExtra("name", friend.getName());
        intent.putExtra("docID", friend.getDocID());
        intent.putExtra("email", friend.getEmail());
        intent.putExtra("notificationToken", friend.getNotificationToken());
        intent.putExtra("myID", user.getUid());

        startActivity(intent);
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
            if(friendsList!=null&&havePerformedQueries&&friendsList.size()==countFriends){
                AddFriendDialogFragment dialog = new AddFriendDialogFragment();
                dialog.show(getSupportFragmentManager(), "add friend dialog");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(String input_email) {
        // User touched the dialog's positive button (i.e. user wants to send a friend request to user with the email entered in the dialog)
        //validate inputted email:
        // first find user that the User wants to send to
        Callback myUserCallback = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                //User with that email found
                afterGetEmail(data, message);
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                //There is no user with that email
                afterFailGetEmail(error, errorCode);
            }
        };
        Utils instance = Utils.getInstance();
        instance.getUserFromEmail(input_email, myUserCallback);
    }

    private void afterFailGetEmail(String error, MyError.ErrorCode errorCode) {
        Toast.makeText(FriendListActivity.this, "That user does not exist. Please ask them to sign up first.", Toast.LENGTH_SHORT).show();
    }

    private void afterGetEmail(Map<String, Object> data, String message) {
        //Check if already friends
        for(dbUser friend:friendsList){
            //I use the notificationToken as the identifier for a user here since dbUser does not have a userID property :p NOTE: I also edited Utils.getUserFromEmail to put notificationToken in map.
            if(friend.getNotificationToken().equals((String) data.get("notificationToken"))){
                Toast.makeText(FriendListActivity.this, "you are already friends", Toast.LENGTH_SHORT).show();
                //Nothing left to do
                return;
            }
        }

        //If we reached here, then not already friends
        //write to Friends collection in Firestore which triggers a notification to be sent to the request recipient
        db.collection("Friends")
                .add(new dbFriends((String) data.get("userID"), user.getUid(), false).getHashmap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(FriendListActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding document", e);
                        Toast.makeText(FriendListActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        //Just close the dialog
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
