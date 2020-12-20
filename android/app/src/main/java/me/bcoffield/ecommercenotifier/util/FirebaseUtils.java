package me.bcoffield.ecommercenotifier.util;

import android.content.Context;
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

    public void uploadToken(Context context) {
        String token = context.getSharedPreferences("default", Context.MODE_PRIVATE).getString("firebaseToken", null);
        if (token == null) {
            Log.e(getClass().getSimpleName(), "No firebase token found");
            return;
        }
        String email = context.getSharedPreferences("default", Context.MODE_PRIVATE).getString("email", null);
        if (email == null) {
            Log.e(getClass().getSimpleName(), "No email found");
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference tokenRef = storage.getReference("firebaseTokens/" + email);
        UploadTask uploadTask = tokenRef.putBytes(token.getBytes());
        uploadTask.addOnSuccessListener(task -> Log.i(getClass().getSimpleName(), "Token upload successful"));
        uploadTask.addOnFailureListener(task -> Log.e(getClass().getSimpleName(), "Token upload failed"));
    }
}
