package me.bcoffield.ecommercenotifier.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

import me.bcoffield.ecommercenotifier.util.FirebaseUtils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUtils.getInstance().uploadToken(token);
    }
}
