package me.bcoffield.ecommercenotifier.util;

import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseUtils {
    private static FirebaseUtils INSTANCE;

    public static FirebaseUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseUtils();
        }
        return INSTANCE;
    }

    public void uploadToken(String token) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference tokenRef = storage.getReference("firebaseTokens/" + token);
        UploadTask uploadTask = tokenRef.putBytes(token.getBytes());
        uploadTask.addOnSuccessListener(task -> Log.i(getClass().getSimpleName(), "Token upload successful"));
        uploadTask.addOnFailureListener(task -> Log.e(getClass().getSimpleName(), "Token upload failed"));
    }
}
