package me.bcoffield.ecommercenotifier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import me.bcoffield.ecommercenotifier.util.FirebaseUtils;

public class SignInActivity extends AppCompatActivity {
    private static final int SIGN_IN_CODE = 37;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            GoogleSignInClient client = GoogleSignIn.getClient(SignInActivity.this, gso);
            Intent signInIntent = client.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                getSharedPreferences("default", MODE_PRIVATE).edit().putString("email", account.getEmail()).apply();
                Log.d(getClass().getSimpleName(), "firebaseAuthWithGoogle:" + account.getIdToken());
                firebaseAuthWithGoogle(account.getIdToken(), account.getEmail());
            } catch (ApiException e) {
                Log.w(getClass().getSimpleName(), "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, String email) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(SignInActivity.this.getClass().getSimpleName(), "signInWithCredential:success");
                        FirebaseUtils.getInstance().uploadToken(SignInActivity.this);
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    } else {
                        // If sign in fails, display sign in button again.
                        Log.w(SignInActivity.this.getClass().getSimpleName(), "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
