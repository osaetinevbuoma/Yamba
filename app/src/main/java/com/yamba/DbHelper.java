package com.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by oevbuoma on 12/23/14.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    // Default constructor
    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    // Called only once  first time we create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s (%s INT PRIMARY KEY, %s TEXT, %s TEXT, %s INT)",
                StatusContract.TABLE, StatusContract.Column.ID, StatusContract.Column.USER, StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT);
        Log.d(TAG, "onCreate with SQL: " + sql);
        db.execSQL(sql); // Execute sql
    }

    // Gets called when existing version is != to new version i.e. schema changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Typically you do ALTER TABLE...
        db.execSQL("DROP TABLE IF EXISTS " + StatusContract.TABLE);
        onCreate(db);
    }
}
