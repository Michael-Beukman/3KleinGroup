package com.sd.a3kleingroup.classes.Sending;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.querying.GetAgreementsUsersFiles;

import java.util.Date;
import java.util.HashMap;

/**
 * This class handles sending the received file.
 */
public class SendReceivedFile {
    private Callback cb;
    private GetAgreementsUsersFiles.FileUserAgreementTriple tripleToSend;
    private String userIDToSendTo;
    private FirebaseFirestore db;

    /**
     *
     * @param cb             The callback that will be called after completion
     * @param tripleToSend   The (File, User, Agreement) that we should send the file of. We only actually use the agreement part, so the others may be fudged
     * @param userIDToSendTo The user to send to.
     * @param db             The database instance.
     */
    public SendReceivedFile(Callback cb, GetAgreementsUsersFiles.FileUserAgreementTriple tripleToSend, String userIDToSendTo, FirebaseFirestore db) {
        this.cb = cb;
        this.tripleToSend = tripleToSend;
        this.userIDToSendTo = userIDToSendTo;
        this.db = db;
    }

    /**
     * Actually does the deed and sends it.
     */
    public void send() {
        Log.d("MY_SENDRECEIVED_FILE", "Actually sending the file");
        // All this does is basically copy the agreement, change the recipient and send it again.
        dbAgreement newAgreement = new dbAgreement(tripleToSend.agreement.getFileID(),          // the same file
                userIDToSendTo,                             // a new user (recipient)
                dbAgreement.getDateInPast(),                // valid until in past
                tripleToSend.agreement.getUserSentID());    // same user that sent it basically.
        db.collection("Agreements")
                .add(newAgreement.getHashmap())
                .addOnSuccessListener(task -> {
                    this.cb.onSuccess(new HashMap<>(), "Success");
                })
                .addOnFailureListener(ex -> {
                    this.cb.onFailure(ex.getMessage(), MyError.ErrorCode.TASK_FAILED);
                });
    }
}
