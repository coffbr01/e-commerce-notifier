package me.bcoffield.ecommercenotifier.list;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

import me.bcoffield.ecommercenotifier.R;
import me.bcoffield.ecommercenotifier.async.ImageLoader;
import me.bcoffield.ecommercenotifier.dto.Notification;

public class NotificationsAdapter extends ArrayAdapter<Notification> {

    private final List<Notification> items;
    private final int resource;
    private final LayoutInflater inflater;

    public NotificationsAdapter(@NonNull Context context, int resource, List<Notification> items) {
        super(context, resource, items);
        this.items = items;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Notification getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }

        Notification item = getItem(position);
        TextView title = convertView.findViewById(R.id.title);
        title.setText(item.getTitle());
        TextView sentTime = convertView.findViewById(R.id.sent_time);
        sentTime.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.DEFAULT).format(new Date(item.getTimestamp())));
        ImageView previewImage = convertView.findViewById(R.id.preview_img);
        new ImageLoader(previewImage).execute(item.getImageUrl());

        return convertView;
    }
}
