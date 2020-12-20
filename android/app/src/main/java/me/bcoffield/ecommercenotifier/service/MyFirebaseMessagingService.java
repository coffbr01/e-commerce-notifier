package me.bcoffield.ecommercenotifier.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

import me.bcoffield.ecommercenotifier.R;
import me.bcoffield.ecommercenotifier.db.DbHandler;
import me.bcoffield.ecommercenotifier.util.FirebaseUtils;

import static me.bcoffield.ecommercenotifier.util.Constants.CHANNEL_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(getClass().getSimpleName(), "New token: ".concat(token));
        getSharedPreferences("default", MODE_PRIVATE).edit().putString("firebaseToken", token).apply();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUtils.getInstance().uploadToken(this);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage);
        saveMessage(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        String url = remoteMessage.getData().get("note");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, browserIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("In Stock!")
                .setContentText(url)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void saveMessage(RemoteMessage remoteMessage) {
        String url = remoteMessage.getData().get("note");
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 5000);
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Unable to fetch ".concat(url));
        }

        String title = null;
        String imageUrl = null;
        if (document != null) {
            title = document.head().selectFirst("meta[property=og:title]").attr("content");
            imageUrl = document.head().selectFirst("meta[property=og:image]").attr("content");
        }
        new DbHandler(this).insertNotification(title, imageUrl, remoteMessage.getSentTime(), url);
    }

}
