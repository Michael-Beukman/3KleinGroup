package com.sd.a3kleingroup.classes.querying;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.SendFileActivity;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.callbacks.ArrayCallback;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Gets agreements, then users and files from agreements.
 */
public class GetAgreementsUsersFiles {
    private String LOG_TAG = "MY_GetAgreementsUsersFiles";
    private Query query;
    private ArrayCallback<FileUserAgreementTriple> callback;
    ArrayList<FileUserAgreementTriple> arr;
    private String userID;
    private int numberOfAgreements = 0;
    private int numberOfAgreementsGotten = 0;
    private FileUserAgreementTriple defaultPair;

    // agreementID => user
    private HashMap<String, dbUser> users;

    // agreementID => file
    private HashMap<String, dbFile> files;
    // agreementID => agreement
    private HashMap<String, dbAgreement> agreements;

    private FirebaseFirestore db;

    public static class FileUserAgreementTriple {
        public dbFile file;
        public dbAgreement agreement;
        public dbUser user;

        public FileUserAgreementTriple(dbFile file, dbAgreement agreement, dbUser user) {
            this.file = file;
            this.agreement = agreement;
            this.user = user;

        }
    }

    /**
     * @param query    The initial query. This needs to be querying the agreement collection.
     * @param callback The callback once everything has been retrieved.
     * @param userID   The user that received everything
     * @param db       The firebase database instance.
     */
    public GetAgreementsUsersFiles(Query query, ArrayCallback<FileUserAgreementTriple> callback, String userID, FirebaseFirestore db) {
        this.query = query;
        this.callback = callback;
        this.userID = userID;
        this.db = db;

        arr = new ArrayList<>();
        files = new HashMap<>();
        users = new HashMap<>();
        agreements = new HashMap<>();
        defaultPair = new FileUserAgreementTriple(
                new dbFile("unknown", "unknown", "unknown", "unknown", "unknown"),
                new dbAgreement("unknown", "unknown", new Date(), "unknown"),
                new dbUser("unknown", "unknown")
        );
    }

    public void getData() {
        query.get()
                .addOnSuccessListener(agreementSnapshots -> {
                    if (agreementSnapshots == null) {
                        callback.onFailure("Data is null", MyError.ErrorCode.TASK_FAILED);
                        return;
                    }
                    // all agreements
                    Map<String, Object> agreements = new HashMap<>();
                    numberOfAgreements = agreementSnapshots.size();
                    if (numberOfAgreements == 0){
                        end();
                    }
                    for (QueryDocumentSnapshot document : agreementSnapshots) {
                        HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                        String fileID = (String) map.get("fileID");
                        String ownerID = (String) map.get("ownerID");
                        Date validUntil = ((Timestamp) map.get("validUntil")).toDate();
                        dbAgreement agreement = new dbAgreement(fileID, userID, validUntil, ownerID);
                        String agreementID = document.getId();

                        agreements.put(agreementID, agreement);
                        getUser(agreementID, agreement.getUserSentID());
                        getFile(agreementID, agreement.getFileID());
                    }
                }).addOnFailureListener(e -> {

        });
    }

    /**
     * Gets a file
     *
     * @param agreementID
     * @param fileID
     */
    private void getFile(String agreementID, String fileID) {
        FirebaseFirestore db = this.db;
        db.collection("Files").document(fileID).get()
                .addOnCompleteListener(task -> {
                    dbFile file;
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getData() != null) {
                        Log.d(LOG_TAG, "We successfully got file " + fileID);
                        Map<String, Object> fileData = task.getResult().getData();
                        file = new dbFile(
                                (String) fileData.getOrDefault("filepath", ""),
                                (String) fileData.getOrDefault("filename", ""),
                                (String) fileData.getOrDefault("userID", ""),
                                (String) fileData.getOrDefault("storageURL", ""),
                                (String) fileData.getOrDefault("encryptionKey", ""));
                    } else {
                        file = defaultPair.file;
                    }

                    files.put(agreementID, file);
                    if (users.containsKey(agreementID)) {
                        endGetAgreement(agreementID);
                    }
                });
    }

    /**
     * Gets a user
     *
     * @param agreementID
     * @param userSentID
     */
    private void getUser(String agreementID, String userSentID) {
        FirebaseFirestore db = this.db;
        db.collection("Users").document(userSentID).get()
                .addOnCompleteListener(task -> {
                    dbUser user;
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getData() != null) {
                        Log.d(LOG_TAG, "We successfully got user " + userSentID);
                        Map<String, Object> userData = task.getResult().getData();
                        user = new dbUser((String) userData.getOrDefault("email", ""), (String) userData.getOrDefault("name", ""));
                    } else {
                        user = defaultPair.user;
                    }

                    users.put(agreementID, user);
                    if (files.containsKey(agreementID)) {
                        endGetAgreement(agreementID);
                    }
                });
    }

    /**
     * Ends the process of getting a single agreement.
     *
     * @param agreementID
     */
    private void endGetAgreement(String agreementID) {
        numberOfAgreementsGotten++;
        arr.add(
                new FileUserAgreementTriple(
                        files.get(agreementID),
                        agreements.get(agreementID),
                        users.get(agreementID)
                )
        );

        if (numberOfAgreementsGotten >= numberOfAgreements) {
            end();
        }
    }

    private void end() {
        callback.onSuccess(arr, "success");
    }
}
