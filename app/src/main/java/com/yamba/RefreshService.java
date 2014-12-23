package com.yamba;

import java.util.List;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {
    private static final String TAG = "com.yamba.RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service - onCreate");
    }

    /*@Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        super.onStartCommand(intent, flag, startId);
        Log.d(TAG, "Service - onStart");
        return START_STICKY;
    }*/

    // Executes on a worker thread
    @Override
    public void onHandleIntent(Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String USERNAME = prefs.getString("username", "");
        final String PASSWORD = prefs.getString("password", "");

        // Check that username and password are not empty
        if (TextUtils.isEmpty(USERNAME) || TextUtils.isEmpty(PASSWORD)) {
            Toast.makeText(this, "Please update your username and password", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Service - onStart");

        // Open up database connection
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        YambaClient yambaClient = new YambaClient(USERNAME, PASSWORD);
        try {
            List<Status> timeline = yambaClient.getTimeline(20);
            for (Status status : timeline) {
                contentValues.clear();

                // Set values for insert
                contentValues.put(StatusContract.Column.ID, status.getId());
                contentValues.put(StatusContract.Column.USER, status.getUser());
                contentValues.put(StatusContract.Column.MESSAGE, status.getMessage());
                contentValues.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());

                // Insert into table
                db.insertWithOnConflict(StatusContract.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

                Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
            }
        } catch (YambaClientException e) {
            Log.e(TAG, "Failed to fetch the timeline", e);
            e.printStackTrace();
        }

        return;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service - onDestroy");
    }
}
