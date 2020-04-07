package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.RecyclerHolder;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;
import com.sd.a3kleingroup.classes.SingleSentFile;
import com.sd.a3kleingroup.classes.User;
import com.sd.a3kleingroup.classes.db.*;
import com.sd.a3kleingroup.classes.messaging.MyFirebaseMessagingService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ReceiveFilesActivity extends BaseActivity {
    final String USER_COLLECTION_NAME = "Users";
    final String FILE_COLLECTION_NAME = "Files";
    Button btnSort;
    EditText edtFilter;

    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    //ArrayList<FileModel> fileModelArrayList = new ArrayList<>();
    RecyclerAdapter myAdapter;
    FirebaseUser user;
    FirebaseFunctions mFirebaseFunctions;
    private static final String LOG_TAG="RecievFilesctivity_LOG";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_receive_files);
        super.onCreate(savedInstanceState);

        setElements();
        setEvents();

        setUpFireStore();
        mFirebaseFunctions = FirebaseFunctions.getInstance();
        determineCurrentUser();
        setUpRV();
        new GoogleApiAvailability().makeGooglePlayServicesAvailable(this);
    }

    private void setEvents() {
        btnSort.setOnClickListener(new Sorter());
        edtFilter.addTextChangedListener(new Filter());
    }

    private void setElements() {
        btnSort = (Button) findViewById(R.id.btnSort);
        edtFilter = (EditText) findViewById(R.id.txtFilter);
    }

    private class Sorter implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showSortPopup(v);
        }

        private void showSortPopup(View v) {
            PopupMenu popup = new PopupMenu(ReceiveFilesActivity.this, v);
            // Inflate the menu from xml
            popup.inflate(R.menu.popup_recfiles_sort);
            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_date:
                            //TODO: sort by date, update recyclerview
                            Toast.makeText(ReceiveFilesActivity.this, "sort by date", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.menu_size:
                            //TODO: sort by size, update recyclerview
                            Toast.makeText(ReceiveFilesActivity.this, "sort by file size", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Handle dismissal with: popup.setOnDismissListener(...);
            // Show the menu
            popup.show();
        }
    }

    private class Filter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // TODO: filter based off ownerID, update recyclerview
            charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private void determineCurrentUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
        }
    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

        RecyclerViewClickListener mTextBtnListener;
        RecyclerViewClickListener mPopUpListener;

        ReceiveFilesActivity receiveFilesActivity;
        ArrayList<dbAgreement> agreements;

        public RecyclerAdapter(ReceiveFilesActivity receiveFilesActivity, QuerySnapshot data, RecyclerViewClickListener txtbtnlistener, RecyclerViewClickListener popuplistener) {
            mTextBtnListener=txtbtnlistener;
            mPopUpListener = popuplistener;
            this.receiveFilesActivity = receiveFilesActivity;
            agreements = new ArrayList<>();
            for (DocumentSnapshot d: data.getDocuments()){
                agreements.add(
                        new dbAgreement((String)d.get("fileID"), (String)d.get("userID"), ((Timestamp)d.get("validUntil")).toDate(), (String)d.get("ownerID"))
                );
            }
        }

        @NonNull
        @Override
        public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(receiveFilesActivity.getBaseContext());
            View view = layoutInflater.inflate(R.layout.view_recycler, null, false);
            return new RecyclerHolder(view, mTextBtnListener, mPopUpListener);
        }

        //Provides a means to handle each view holder in the list
        @Override
        public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

            dbAgreement agreement = agreements.get(position);
            holder.joinedFileInfo.setAgreement(agreement);
            //set text of views asynchronously

            Log.d(LOG_TAG, "Binding " + position + agreement.getUserSentID());

            // before we query, make text 'loading...' on holder
            holder.setLoading();

            holder.txtDate.setText(agreement.getValidUntil().toString());
            // get Username
            getAsync(USER_COLLECTION_NAME, agreement.getUserSentID(), holder.cbOwner);
            // get filename
            getAsync(FILE_COLLECTION_NAME, agreement.getFileID(), holder.cbFile);
        }

        @Override
        public int getItemCount() {
            return agreements.size();
        }
    }

    protected void getAsync(String collectionName, String docID, Callback cb){
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


//    private void getDataFromFirebase() {
//        if(fileModelArrayList.size()>0)
//            fileModelArrayList.clear();
//
//        String userID = user.getUid();
//
//        // [START get_multiple]
//        db.collection("Agreements")
//                .whereEqualTo("userID", userID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            HashMap<String,dbAgreement> agreements = new HashMap<>();
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
//                                String fileID = (String) map.get("fileID");
//                                String userID = (String) map.get("ownerID");
//                                Date validUntil = ((Timestamp )map.get("validUntil")).toDate();
//                                String userSentID = (String) map.get("userID");
//                                dbAgreement agreement = new dbAgreement(fileID, userID, validUntil, userID);
//                                agreements.put(fileID, agreement);
//                            }
//
//                            // [START get_url's]
//                            db.collection("Files")
//                                    .get()
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            if (task.isSuccessful()) {
//                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                    if(agreements.containsKey((String) document.getId())){
//                                                        Log.e(LOG_TAG, agreements.get((String) document.getId()).getFileID());
//                                                        HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
//                                                        FileModel file = new FileModel();
//                                                        file.setFileName((String) map.get("filename"));
//                                                        file.setFormat("");
//                                                        file.setPath((String) map.get("filepath"));
//                                                        file.setUrl((String) map.get("storageURL"));
//                                                        file.setAgreement(agreements.get((String) document.getId()));
//
//                                                        //Get Owner details
//                                                        db.collection("Users").document((String) map.get("userID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                DocumentSnapshot d = task.getResult();
//                                                                User owner = new User((String) d.getData().get("email"),(String) d.getData().get("name"),(String) d.getData().get("notificationToken"), (String) map.get("userID"));
//                                                                file.setOwner(owner);
//                                                                fileModelArrayList.add(file);
//                                                                //TODO: find another wy to do this. This seems very bad
//                                                                setUpRV();
//                                                            }
//                                                        });
//                                                    }
//                                                }
//                                                //setUpRV(); does not work here since it is called before the db.collection("Users") collection is fetched from firebase. How to get around this? Need to implement callbacks but not sure how
//                                            } else {
//                                                Log.e(LOG_TAG, "Error getting documents: ", task.getException());
//                                            }
//                                        }
//                                    });
//                            // [END get_url's]
//
//                        } else {
//                            Log.e(LOG_TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        // [END get_multiple]
//
//    }

    private void setUpFireStore(){
        db = FirebaseFirestore.getInstance();
    }


    public void downloadFile(FileModel file) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(file.getPath());

        File localFile = null;
        try {
            //TODO check that filename is valid
            localFile = File.createTempFile("temp", null,null);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }


        fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.e(LOG_TAG, taskSnapshot.getBytesTransferred()+"");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(LOG_TAG, exception.getMessage());
            }
        });
        Log.e(LOG_TAG, localFile.getAbsolutePath());

        //Google download manager
//        DownloadManager downloadmanager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri = Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalFilesDir(this, destinationDirectory, fileName + fileExtension);
//
//        downloadmanager.enqueue(request);
    }


    private void setUpRV(){
        Query query = db.collection("Agreements").whereEqualTo("userID", user.getUid());
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager viewManager = new LinearLayoutManager(this);

        RecyclerViewClickListener textButtonListener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position, RecyclerHolder holder) {
                if(holder.joinedFileInfo.isAllDataRetrieved()){
                    FileModel file = holder.joinedFileInfo;
                    sendNotificationRequestingPermission(file.getFileName(), file.getOwner().getNotificationToken());
                }
                //downloadFile(file);
            }
        };

        RecyclerViewClickListener popUpListener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position, RecyclerHolder holder) {
                if(holder.joinedFileInfo.isAllDataRetrieved()){
                    showHamburgerPopUp(view, position, holder.joinedFileInfo);
                }
            }
        };

        // Add a snapshot listener, so that it updates live
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e == null){
                myAdapter = new ReceiveFilesActivity.RecyclerAdapter(ReceiveFilesActivity.this, queryDocumentSnapshots, textButtonListener, popUpListener);
                mRecyclerView.setAdapter(myAdapter);

                mRecyclerView.setLayoutManager(viewManager);
            }else{
                // todo error
                Log.d(LOG_TAG, "Error at query snapshotlistener "+ e.getMessage());
            }
        });
    }

    private void sendNotificationRequestingPermission(String fName, String ownerToken) {
        // Create the arguments to the callable function.
        Log.d("Notification", fName + " " + ownerToken);
        Map<String, Object> data = new HashMap<>();
        data.put("userName", user.getDisplayName());
        data.put("fileName", fName);
        data.put("ownerToken", ownerToken);
        data.put("push", true);

        mFirebaseFunctions
                .getHttpsCallable("requestPermission")
                .call(data);

    }

    private void showHamburgerPopUp(View v, int position, FileModel file) {
        PopupMenu popup = new PopupMenu(ReceiveFilesActivity.this, v);
        // Inflate the menu from xml
        popup.inflate(R.menu.popup_received_files_hamurger);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_share:
                        //TODO: share file
                        Toast.makeText(ReceiveFilesActivity.this, "share file " + file.getFileName(), Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_details:
                        //TODO: show file details in pop up
                        Toast.makeText(ReceiveFilesActivity.this, "show details of file " + file.getFileName(), Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }
}
