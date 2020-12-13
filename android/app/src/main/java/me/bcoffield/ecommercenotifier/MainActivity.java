package me.bcoffield.ecommercenotifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import me.bcoffield.ecommercenotifier.util.FirebaseUtils;

import static me.bcoffield.ecommercenotifier.util.Constants.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_CODE = 37;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .build();
            findViewById(R.id.sign_in_button).setOnClickListener(v -> {
                GoogleSignInClient client = GoogleSignIn.getClient(MainActivity.this, gso);
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_CODE);
            });
        }
        toggleSignIn(currentUser == null);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(getClass().getSimpleName(), "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(getClass().getSimpleName(), "Google sign in failed", e);
            }
        }
    }

    private void toggleSignIn(boolean signIn) {
        findViewById(R.id.sign_in_button).setVisibility(signIn ? View.VISIBLE : View.GONE);
        findViewById(R.id.all_set_text).setVisibility(signIn ? View.GONE : View.VISIBLE);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(MainActivity.this.getClass().getSimpleName(), "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        toggleSignIn(user == null);
                        FirebaseUtils.getInstance().uploadToken(MainActivity.this);
                    } else {
                        // If sign in fails, display sign in button again.
                        Log.w(MainActivity.this.getClass().getSimpleName(), "signInWithCredential:failure", task.getException());
                        toggleSignIn(true);
                    }
                });
    }
}