package me.bcoffield.ecommercenotifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import me.bcoffield.ecommercenotifier.db.DbHandler;
import me.bcoffield.ecommercenotifier.dto.Notification;
import me.bcoffield.ecommercenotifier.list.NotificationsAdapter;

import static me.bcoffield.ecommercenotifier.util.Constants.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            return;
        }
        setContentView(R.layout.activity_main);
        DbHandler db = new DbHandler(this);
        List<Notification> notifications = db.getNotifications();
        ListView lv = (ListView) findViewById(R.id.notifications_list);
        TextView emptyText = (TextView)findViewById(R.id.notifications_list_empty);
        lv.setEmptyView(emptyText);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            Notification notification = (Notification) parent.getItemAtPosition(position);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(notification.getUrl()));
            MainActivity.this.startActivity(browserIntent);
        });
        ListAdapter adapter = new NotificationsAdapter(this, R.layout.notifications_list_row, notifications);
        lv.setAdapter(adapter);
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

}