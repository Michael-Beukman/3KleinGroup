package com.sd.a3kleingroup.classes;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility Class.
 */
public class Utils {

    private static Utils instance;


    private Utils(){ }
    public static Utils getInstance(){
        if (instance == null) instance = new Utils();
        return instance;
    }


    /**
     * Looks in the firebase DB for a user with the specified email.
     * @param email The email to look for
     * @param cb The Callback to run after finding the data
     */
    public void getUserFromEmail(String email, Callback cb){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // look for the email.
        db.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                QuerySnapshot documents = task.getResult();
                int size = documents.size();
                if (size == 0){
                    // then no users match that email
                    cb.onFailure("No Matching Email", MyError.ErrorCode.NOT_FOUND);
                }else{
                    // then there has to be one user that matches this email
                    for (QueryDocumentSnapshot doc: documents){
                        final String userID = doc.getId();
                        cb.onSuccess(new HashMap<String, Object>(){{
                            put("userID", userID);
                        }}, "Success");
                        // stop, since we got our user.
                        break;
                    }
                }
            }else{
                cb.onFailure(task.getException().getMessage(), MyError.ErrorCode.TASK_FAILED);
            }
        });


    }
}
