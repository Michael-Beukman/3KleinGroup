package com.sd.a3kleingroup.classes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sd.a3kleingroup.classes.callbacks.CallbackGeneric;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility Class.
 */
public class Utils {

    private static Utils instance;


    private Utils() {
    }

    public static Utils getInstance() {
        if (instance == null) instance = new Utils();
        return instance;
    }


    /**
     * Looks in the firebase DB for a user with the specified email.
     *
     * @param email The email to look for
     * @param cb    The Callback to run after finding the data
     */
    public void getUserFromEmail(String email, Callback cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // look for the email.
        db.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                int size = documents.size();
                if (size == 0) {
                    // then no users match that email
                    cb.onFailure("No Matching Email", MyError.ErrorCode.NOT_FOUND);
                } else {
                    // then there has to be one user that matches this email
                    for (QueryDocumentSnapshot doc : documents) {
                        final String userID = doc.getId();
                        cb.onSuccess(new HashMap<String, Object>() {{
                            put("userID", userID);
                            put("notificationToken", doc.getData().get("notificationToken"));
                        }}, "Success");
                        // stop, since we got our user.
                        break;
                    }
                }
            } else {
                cb.onFailure(task.getException().getMessage(), MyError.ErrorCode.TASK_FAILED);
            }
        });
    }

    /**
     * Looks in the firebase DB for a user with the specified userID.
     *
     * @param ID The email to look for
     * @param cb The Callback to run after finding the data
     */
    public void getUserFromID(String ID, CallbackGeneric<dbUser> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // look for the email.
        db.collection("Users").document(ID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                DocumentSnapshot document = task.getResult();
                // then there has to be one user that matches this email
                cb.onSuccess(new dbUser(document.getString("email"), document.getString("name")), "Success");
            } else {
                cb.onFailure(task.getException().getMessage(), MyError.ErrorCode.TASK_FAILED);
            }
        });
    }

    /**
     * Converts the m to a string hashmap. Tries to cast each param as string. If that fails, use .toString().
     * @param m
     * @return
     */
    public HashMap<String, String> toStringHashMap(Map<String, Object> m){
        HashMap<String, String> ans = new HashMap<>();
        for (String key: m.keySet()){
            Object val = m.get(key);
            if (val == null) val = "";
            try{
                ans.put(key, (String)val);
            }catch (Exception e){
                ans.put(key, val.toString());
            }
        }
        return ans;
    }
}
