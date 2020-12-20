package me.bcoffield.ecommercenotifier.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.bcoffield.ecommercenotifier.dto.Notification;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "notificationsdb";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_URL = "url";

    public DbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE_URL + " TEXT,"
                + KEY_TIMESTAMP + " INTEGER,"
                + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        // Create tables again
        onCreate(db);
    }

    public void insertNotification(String title, String imageUrl, long timestamp, String url) {
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_TITLE, title);
        cValues.put(KEY_IMAGE_URL, imageUrl);
        cValues.put(KEY_TIMESTAMP, timestamp);
        cValues.put(KEY_URL, url);
        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_NOTIFICATIONS, null, cValues);
        db.close();
    }

    public List<Notification> getNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + KEY_TIMESTAMP + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Notification item = new Notification();
            item.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            item.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            item.setImageUrl(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)));
            item.setTimestamp(cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP)));
            item.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
            notifications.add(item);
        }
        cursor.close();
        return notifications;
    }
}
