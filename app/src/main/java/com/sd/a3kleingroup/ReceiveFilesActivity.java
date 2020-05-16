package com.sd.a3kleingroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.internal.firebase_auth.zzew;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.RecyclerHolder;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;
import com.sd.a3kleingroup.classes.Sending.SendReceivedFile;
import com.sd.a3kleingroup.classes.UI.ReceiveFilesSorter;
import com.sd.a3kleingroup.classes.Utils;
import com.sd.a3kleingroup.classes.db.*;
import com.sd.a3kleingroup.classes.encryption.AESEncryption;
import com.sd.a3kleingroup.classes.querying.GetAgreementsUsersFiles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveFilesActivity extends BaseActivity {
    final String USER_COLLECTION_NAME = "Users";
    final String FILE_COLLECTION_NAME = "Files";
    Button btnSort;
    EditText edtFilter;

    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    RecyclerAdapter myAdapter;
    FirebaseUser user;
    FirebaseFunctions mFirebaseFunctions;
    ReceiveFilesSorter mySorter = null;
    Filter myFilter = new Filter();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG_TAG = "RecievFilesctivity_LOG";
        setContentView(R.layout.activity_receive_files);
        super.onCreate(savedInstanceState);

        setElements();
        mySorter = new ReceiveFilesSorter(null, ReceiveFilesActivity.this);
        setEvents();

        setUpFireStore();
        mFirebaseFunctions = FirebaseFunctions.getInstance();
        //determineCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            loginTempUser(new Callback() {
                @Override
                public void onSuccess(Map<String, Object> data, String message) {
                    setUpRV();
                }

                @Override
                public void onFailure(String error, MyError.ErrorCode errorCode) {
                    // todo
                }
            });
        }else{
            setUpRV();
        }
    }


    private class Filter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d(LOG_TAG, "In filter " + charSequence.toString());
            String toFind = charSequence.toString().toLowerCase();
            if (myAdapter == null) return;
            ;
            myAdapter.filteredAgreements = new ArrayList<>();
            for (dbAgreement a : myAdapter.OGagreements) {
                if (RecyclerHolder.cache.containsKey(a.getId())) {
                    // then we can possibly filter on this.
                    FileModel f = RecyclerHolder.cache.get(a.getId());
                    if (f.getOwner().getName().toLowerCase().contains(toFind) || f.getFileName().toLowerCase().contains(toFind)) {
                        Log.d(LOG_TAG, "user is " + f.getOwner().getName() + " USER " + f.getOwner().getName().contains(charSequence) + " file " + f.getFileName().contains(charSequence) + " File name is " + f.getFileName());
                        myAdapter.filteredAgreements.add(a);
                    }
                }
                //else we cannot do anything
            }
            Log.d(LOG_TAG, "After filter our dataset has " + myAdapter.filteredAgreements.size() + " and looks like " + Arrays.toString(myAdapter.filteredAgreements.toArray()));
            myAdapter.onChangeDataset();

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

        RecyclerViewClickListener mTextBtnListener;
        RecyclerViewClickListener mPopUpListener;

        ReceiveFilesActivity receiveFilesActivity;
        public ArrayList<dbAgreement> OGagreements;
        public ArrayList<dbAgreement> filteredAgreements;


        public RecyclerAdapter(ReceiveFilesActivity receiveFilesActivity, QuerySnapshot data, RecyclerViewClickListener txtbtnlistener, RecyclerViewClickListener popuplistener) {
            mTextBtnListener = txtbtnlistener;
            mPopUpListener = popuplistener;
            this.receiveFilesActivity = receiveFilesActivity;
            OGagreements = new ArrayList<>();
            filteredAgreements = new ArrayList<>();
            for (DocumentSnapshot d : data.getDocuments()) {
                dbAgreement toAdd = new dbAgreement((String) d.get("fileID"), (String) d.get("userID"), ((Timestamp) d.get("validUntil")).toDate(), (String) d.get("ownerID"));
                // sets the Id, to make sure we have a reference to the agreement
                toAdd.setID(d.getId());
                OGagreements.add(toAdd);
                filteredAgreements.add(toAdd);
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

            dbAgreement agreement = filteredAgreements.get(position);
            holder.joinedFileInfo.setAgreement(agreement);
            //set text of views asynchronously

            Log.d(LOG_TAG, "Binding " + position + agreement.getUserSentID());

            // before we query, make text 'loading...' on holder
            holder.setLoading();

            holder.txtDate.setText(agreement.getValidUntil().toString());

            if (!RecyclerHolder.cache.containsKey(agreement.getId())) {
                // if the cache doesn't contain the data, i.e. we haven't gotten file/owner, get it.
                // get Username
                getAsync(USER_COLLECTION_NAME, agreement.getUserSentID(), holder.cbOwner);
                // get filename
                getAsync(FILE_COLLECTION_NAME, agreement.getFileID(), holder.cbFile);
            } else {
                // we already have it.
                holder.populateViews(RecyclerHolder.cache.get(agreement.getId()));
            }
            Log.d(LOG_TAG, "Holder at position " + position);
        }

        @Override
        public int getItemCount() {
            return filteredAgreements.size();
        }

        /**
         * Notifies that the dataset changed.
         */
        public void onChangeDataset() {
            notifyDataSetChanged();
        }
    }


    private void setUpFireStore() {
        db = FirebaseFirestore.getInstance();
    }


    /* FILE THINGS */

    public void downloadFile(FileModel file) {

        //TODO: This is broken now even just with pdf even though didn't really change anything. Getting error java.io.IOException: javax.crypto.BadPaddingException: pad block corrupted
        //Does this mean the key is getting corrupted?
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(file.getPath());
        fileRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String mime = storageMetadata.getContentType();
                Log.d(LOG_TAG, mime);
                //We're just gonna assume it's a pdf or an image

                final long ONE_MEGABYTE = 1024 * 1024 * 10;
                fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    //bytes is of type byte[]
                    AESEncryption decryptor = new AESEncryption(file.getEncryptionKey());
                    InputStream stream = decryptor.decrypt(new ByteArrayInputStream(bytes));
                    try {
                        //Create temp file to store file so that external apps can be used to open it.
                        File tmpFile = File.createTempFile("tmp", ".pdf");
                        //OutputStream outputStream = new FileOutputStream(tmpFile);
                        java.nio.file.Files.copy(
                                stream,
                                tmpFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                        //IOUtils.copyStream(stream, outputStream);

                        //Open file with external app
                        //For why FileProvider used, see: https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
                        Uri path = FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".provider", tmpFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                        tmpFile.deleteOnExit();
                        //outputStream.flush();
                        //outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    private void checkPermission(FileModel file) {
        //Check if permission expired
        if (file.getAgreement().getValidUntil().before(new Date())) {
            //permission expired, request new permission
            Toast.makeText(ReceiveFilesActivity.this, "Permission Expired. Request Sent", Toast.LENGTH_SHORT).show();
            sendNotificationRequestingPermission(file.getFileName(), file.getOwner().getNotificationToken());
        } else {
            downloadFile(file);
        }
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
    /* END FILE THINGS */

    /* EVENTS AND ELEMENTS */

    private void setEvents() {

        // sorting gets set up when the data comes.
         btnSort.setOnClickListener(mySorter);

        edtFilter.addTextChangedListener(myFilter);
    }

    private void setElements() {
        btnSort = (Button) findViewById(R.id.btnSort);
        edtFilter = (EditText) findViewById(R.id.txtFilter);
    }

    private void showHamburgerPopUp(View v, int position, FileModel file) {
        PopupMenu popup = new PopupMenu(ReceiveFilesActivity.this, v);

        // Inflate the menu from xml
        popup.inflate(R.menu.popup_received_files_hamurger);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_share:
                    //TODO: share file
                    Toast.makeText(ReceiveFilesActivity.this, "share file " + file.getFileName(), Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Tried to Share and clicked the following position " + position + " " + file);
                    shareFile(file);
                    return true;
                case R.id.menu_details:
                    //TODO: show file details in pop up
                    Toast.makeText(ReceiveFilesActivity.this, "show details of file " + file.getFileName(), Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    private void setUpRV() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            user = new FirebaseUser() {
                @NonNull
                @Override
                public String getUid() {
                    return "";
                }

                @NonNull
                @Override
                public String getProviderId() {
                    return null;
                }

                @Override
                public boolean isAnonymous() {
                    return false;
                }

                @Nullable
                @Override
                public List<String> zza() {
                    return null;
                }

                @NonNull
                @Override
                public List<? extends UserInfo> getProviderData() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                    return null;
                }

                @Override
                public FirebaseUser zzb() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseApp zzc() {
                    return null;
                }

                @Nullable
                @Override
                public String getDisplayName() {
                    return null;
                }

                @Nullable
                @Override
                public Uri getPhotoUrl() {
                    return null;
                }

                @Nullable
                @Override
                public String getEmail() {
                    return null;
                }

                @Nullable
                @Override
                public String getPhoneNumber() {
                    return null;
                }

                @Nullable
                @Override
                public String zzd() {
                    return null;
                }

                @NonNull
                @Override
                public zzew zze() {
                    return null;
                }

                @Override
                public void zza(@NonNull zzew zzew) {

                }

                @NonNull
                @Override
                public String zzf() {
                    return null;
                }

                @NonNull
                @Override
                public String zzg() {
                    return null;
                }

                @Nullable
                @Override
                public FirebaseUserMetadata getMetadata() {
                    return null;
                }

                @NonNull
                @Override
                public zzz zzh() {
                    return null;
                }

                @Override
                public void zzb(List<zzy> list) {

                }

                @Override
                public void writeToParcel(Parcel parcel, int i) {

                }

                @Override
                public boolean isEmailVerified() {
                    return false;
                }
            };
        }
        Query query = db.collection("Agreements").whereEqualTo("userID", user.getUid());
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager viewManager = new LinearLayoutManager(this);

        RecyclerViewClickListener textButtonListener = (view, position, holder) -> {
            //make sure all data for this file has been retrieved from firebase
            if (holder.joinedFileInfo.isAllDataRetrieved()) {
                FileModel file = holder.joinedFileInfo;
                checkPermission(file);
            }
        };

        RecyclerViewClickListener popUpListener = (view, position, holder) -> {
            //make sure all data for this file has been retrieved from firebase
            Log.d(LOG_TAG, "Clickec thingy " + holder.joinedFileInfo.isAllDataRetrieved());
            if (holder.joinedFileInfo.isAllDataRetrieved()) {
                showHamburgerPopUp(view, position, holder.joinedFileInfo);
            }
        };

        // Add a snapshot listener, so that it updates live
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e == null) {
                myAdapter = new ReceiveFilesActivity.RecyclerAdapter(ReceiveFilesActivity.this, queryDocumentSnapshots, textButtonListener, popUpListener);
                mRecyclerView.setAdapter(myAdapter);
                mRecyclerView.setLayoutManager(viewManager);
                if (mySorter.getAdapter() == null) {
                    mySorter.setAdapter(myAdapter);
                }
            } else {
                // todo error
                Log.d(LOG_TAG, "Error at query snapshotlistener " + e.getMessage());
            }
        });
    }

    /* END EVENTS AND ELEMENTS */


    /**
     * Shares a (received) file. Does popup stuff to get emails and send the received file.
     *
     * @param file
     */
    private void shareFile(FileModel file) {
        class doShareFile {
            private String userIDToSendTo = null;
            private AlertDialog emailPopup = null;
            /** The callback that will happen after getting an id from an email successfully or not*/
            private Callback afterGetUserEmailCallback;
            private MyError handler;

            public doShareFile() {
                handler = new MyError(getApplication());
                afterGetUserEmailCallback = new Callback() {
                    @Override
                    public void onSuccess(Map<String, Object> data, String message) {
                        getEmail(data, message);
                    }

                    @Override
                    public void onFailure(String error, MyError.ErrorCode errorCode) {
                        failToGetEmail(error, errorCode);
                    }
                };
            }

            /**
             * This creates a dialog box with input for email, to get the user ID.
             */
            public void makeEmailDialog() {

                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveFilesActivity.this);

                builder.setTitle("Choose a user to send to.");

                // add OK and Cancel buttons
                final EditText textInput = new EditText(ReceiveFilesActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                builder.setView(textInput);
                textInput.setLayoutParams(lp);

                // add OK and Cancel buttons
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // user clicked OK
                    Log.d(LOG_TAG, "We said OK to the email dialog");
                    String emailToGet = String.valueOf(textInput.getText());
                    Utils.getInstance().getUserFromEmail(emailToGet, afterGetUserEmailCallback);

                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // we didn't choose anything
                    Log.d(LOG_TAG, "Did not choose an email to send to. Not doing anything");
                });

                builder.setView(textInput);
                // create and show the alert dialog

                emailPopup = builder.create();
                emailPopup.show();

            }

            boolean readyToSend() {
                return userIDToSendTo != null;
            }

            void getEmail(Map<String, Object> mData, String message) {
                Log.d(LOG_TAG, "We received the userID from the email " + message + " - " + mData.toString());
                if (!mData.containsKey("userID")) {
                    failToGetEmail("Could not get email from map", MyError.ErrorCode.NOT_FOUND);
                    return;
                }

                userIDToSendTo = (String) mData.get("userID");
                // now, given that we have the userID, we can send easily
                sendFile();
            }

            /**
             * This actually send the file to the other user
             */
            private void sendFile() {
                // first we convert a filemodel to a triple of file, agreement and user
                GetAgreementsUsersFiles.FileUserAgreementTriple toSend = new GetAgreementsUsersFiles.FileUserAgreementTriple(
                        new dbFile(file.getPath(), file.getFileName(), "", file.getUrl(), file.getEncryptionKey()),
                        file.getAgreement(), file.getOwner()
                );
                Callback cb = new Callback() {
                    @Override
                    public void onSuccess(Map<String, Object> data, String message) {
                        // we successfully sent it
                        Log.d(LOG_TAG, "Successfully sent the received file");
                        handler.displaySuccess("Successfully sent the received file");
                    }

                    @Override
                    public void onFailure(String error, MyError.ErrorCode errorCode) {
                        Log.d(LOG_TAG, "Failed to send the received file (" + error + ")");
                        handler.displayError("Failed to send the received file (" + error + ")");
                    }
                };

                SendReceivedFile sender = new SendReceivedFile(cb, toSend, userIDToSendTo, FirebaseFirestore.getInstance());
                sender.send();
            }

            private void failToGetEmail(String error, MyError.ErrorCode errorCode) {
                // todo make an error
                handler.displayError(error);
            }
        }

        doShareFile f = new doShareFile();
        f.makeEmailDialog();

    }
}
