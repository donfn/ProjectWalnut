package gr.fragment.projectwalnut.Core;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class API {

    private static FirebaseAuth mAuth;
    private static FirebaseUser user;
    private static FirebaseFirestore db;

    private static final String TAG = "ProjectWalnut";

    public static void init(String email, String password){
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener((Activity) System.mContext, (OnCompleteListener<AuthResult>) task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "signInWithEmail:success");
                    user = mAuth.getCurrentUser();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithEmail:failure", task.getException());
                }
            });
    }

    private static FirebaseUser getUser(){
        return user;
    }

    private static void updateData(String collection, String document, Map<String, Object> values){
        db.collection(collection).document(document)
            .update(values)
            .addOnSuccessListener(documentReference -> {
                Log.e(TAG, "updateArm:success");
                Toast.makeText(System.mContext, "updateArm:success", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "updateArm:failure");
                Toast.makeText(System.mContext, "updateArm:failure", Toast.LENGTH_SHORT).show();
            });
    }

    public static void armHouse(boolean state){
        Map<String, Object> data = new HashMap<>();
        data.put("armed", state);

        updateData("misc", "config", data);
    }

    private static Map<String, Object> getData(String collection, String document){
        final Map<String, Object>[] map = new Map[]{new HashMap<>()};

        db.collection(collection).document(document)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) map[0] = task.getResult().getData();
                    else map[0] = null;
                });

        return map[0];
    }

    public static boolean getArmed(){
        Map<String, Object> data = getData("misc", "config");
        Log.e(TAG, ""+data);
        return false;
    }

}