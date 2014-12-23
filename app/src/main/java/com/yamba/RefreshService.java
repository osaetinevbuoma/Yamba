package com.yamba;

import java.util.List;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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

        YambaClient yambaClient = new YambaClient(USERNAME, PASSWORD);
        try {
            List<Status> timeline = yambaClient.getTimeline(20);
            for (Status status : timeline) {
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
